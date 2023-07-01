package usyd.mingyi.animalcare.service.serviceImp;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import java.util.concurrent.ConcurrentMap;

@Service
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
            messages.put(correlationId,responseMessage);
            rabbitTemplate.convertAndSend(MQConfig.MESSAGE_EXCHANGE,"#",objectMapper.writeValueAsString(responseMessage), message -> {
                MessageProperties properties = message.getMessageProperties();
                properties.setDeliveryMode(MessageDeliveryMode.PERSISTENT);
                return message;
            },new CorrelationData());
        } catch (JsonProcessingException e) {
            throw new CustomException("System Error");
        }
    }

    private RabbitTemplate.ConfirmCallback confirmCallback() {
        return (correlationData, ack, cause) -> {
            correlationData.getId();
            System.out.println(correlationData);
            if (ack) {
                //System.out.println("Message successfully published.");
            } else {
                System.out.println("Failed to publish message: " + cause);
                //requeueMessage(correlationData.getReturnedMessage());
            }
        };
    }

    private void requeueMessage(Message returnedMessage) {
        rabbitTemplate.send(returnedMessage.getMessageProperties().getReceivedExchange(),
                returnedMessage.getMessageProperties().getReceivedRoutingKey(),
                returnedMessage);
    }
}
