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

import usyd.mingyi.animalcare.mongodb.entity.CloudMessage;
import usyd.mingyi.animalcare.mongodb.service.CloudMessageService;
import usyd.mingyi.animalcare.socketEntity.ChatMessage;
import usyd.mingyi.animalcare.service.ChatService;
import usyd.mingyi.animalcare.utils.CommonUtils;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class ChatServiceImp implements ChatService {
    @Resource
    private ObjectMapper objectMapper;

    private final RabbitTemplate rabbitTemplate;

    @Resource
    private CloudMessageService cloudMessageService;

    private static final ConcurrentHashMap<String,ChatMessage> messages = new ConcurrentHashMap<>();
    @Autowired
    public ChatServiceImp(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
        rabbitTemplate.setConfirmCallback(confirmCallback());
    }


    // 生成聊天ID


    @Override
    public CloudMessage retrieveDataFromMongoDB(String fromId, String toId) {
        return cloudMessageService.getCloudMessageById(CommonUtils.combineId(fromId,toId));
    }

    @Override
    public CloudMessage retrieveDataFromMongoDB(Long fromId, Long toId) {
       return retrieveDataFromMongoDB(String.valueOf(fromId),String.valueOf(toId));
    }

    @Override
    public List<CloudMessage> retrieveAllDataFromMongoDB(String userId) {
        return cloudMessageService.getChatRecords(userId);
    }

    @Override
    public List<CloudMessage> retrievePartlyDataFromMongoDB(String userId,  Map<String,Long>  localStorage) {
          return cloudMessageService.getChatRecordsPartly(userId,localStorage);
    }

    public void sendMsgToQueue(ChatMessage chatMessage){
        try {
            String correlationId = UUID.randomUUID().toString();
            //log.info("入队消息ID: {}",correlationId);
            messages.put(correlationId,chatMessage);
            rabbitTemplate.convertAndSend(MQConfig.MESSAGE_EXCHANGE,"#",objectMapper.writeValueAsString(chatMessage), message -> {
                MessageProperties properties = message.getMessageProperties();
                properties.setDeliveryMode(MessageDeliveryMode.PERSISTENT);
                return message;
            },new CorrelationData(correlationId));
        } catch (JsonProcessingException e) {
            throw new CustomException("System Error");
        }
    }

    @Override
    public void saveMsgInCloud(ChatMessage chatMessage) {
        cloudMessageService.insertMsg(chatMessage);
    }

    private RabbitTemplate.ConfirmCallback confirmCallback() {
        return (correlationData, ack, cause) -> {
            //log.info("成功入队消息ID: {}",correlationData.getId());
            if(correlationData==null){
                return;
            }
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
