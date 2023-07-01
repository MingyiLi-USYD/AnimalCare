package usyd.mingyi.animalcare.service.serviceImp;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import usyd.mingyi.animalcare.common.CustomException;
import usyd.mingyi.animalcare.config.rabbitMQ.MQConfig;
import usyd.mingyi.animalcare.mapper.ChatMapper;
import usyd.mingyi.animalcare.pojo.chat.ResponseMessage;
import usyd.mingyi.animalcare.service.ChatService;

import javax.annotation.Resource;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class ChatServiceImp implements ChatService {
    @Resource
    private ObjectMapper objectMapper;
    @Resource
    private ChatMapper chatMapper;

    private final RabbitTemplate rabbitTemplate;

    private static final ConcurrentHashMap<String,ResponseMessage> messages = new ConcurrentHashMap<>();
    @Autowired
    public ChatServiceImp(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
        rabbitTemplate.setConfirmCallback(confirmCallback());
    }

    @Override
    public void sendMsgToFirebase(String currentId,String toId, ResponseMessage responseMessage) {
        chatMapper.sendMsgToFirebase(currentId,toId,responseMessage);
    }


    public CompletableFuture<List<usyd.mingyi.animalcare.pojo.chat.Message>> retrieveDataFromFirebase(String fromId, String toId) {
           return chatMapper.retrieveDataFromFirebase(fromId,toId);
    }
    // 生成聊天ID

    public void sendMsgToQueue(ResponseMessage responseMessage){
        try {
            String correlationId = UUID.randomUUID().toString();
            //log.info("入队消息ID: {}",correlationId);
            messages.put(correlationId,responseMessage);
            rabbitTemplate.convertAndSend(MQConfig.MESSAGE_EXCHANGE,"#",objectMapper.writeValueAsString(responseMessage), message -> {
                MessageProperties properties = message.getMessageProperties();
                properties.setDeliveryMode(MessageDeliveryMode.PERSISTENT);
                return message;
            },new CorrelationData(correlationId));
        } catch (JsonProcessingException e) {
            throw new CustomException("System Error");
        }
    }

    private RabbitTemplate.ConfirmCallback confirmCallback() {
        return (correlationData, ack, cause) -> {
            //log.info("成功入队消息ID: {}",correlationData.getId());
            if (ack) {
                if(messages.contains(correlationData.getId())){
                    messages.remove(correlationData.getId());
                }

            } else {
                if(messages.contains(correlationData.getId())){
                    sendMsgToQueue(messages.get(correlationData.getId()));
                   messages.remove(correlationData.getId());
                }
            }
        };
    }

    private void requeueMessage(Message returnedMessage) {
        rabbitTemplate.send(returnedMessage.getMessageProperties().getReceivedExchange(),
                returnedMessage.getMessageProperties().getReceivedRoutingKey(),
                returnedMessage);
    }
}
