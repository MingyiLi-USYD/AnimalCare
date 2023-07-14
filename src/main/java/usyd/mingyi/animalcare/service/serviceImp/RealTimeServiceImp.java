package usyd.mingyi.animalcare.service.serviceImp;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import usyd.mingyi.animalcare.common.CustomException;
import usyd.mingyi.animalcare.config.rabbitMQ.MQConfig;
import usyd.mingyi.animalcare.service.RealTimeService;
import usyd.mingyi.animalcare.socketEntity.ServiceMessage;
import usyd.mingyi.animalcare.socketEntity.SystemMessage;

import javax.annotation.Resource;
import org.springframework.amqp.core.Message;

@Service
@Slf4j
public class RealTimeServiceImp implements RealTimeService {
    @Value("${serverId}")
    private String serverId;
    @Resource
    private ObjectMapper objectMapper;

    @Resource
    private RabbitTemplate rabbitTemplate;


    @Override
    public void remindFriends(ServiceMessage serviceMessage) {

        try {
            //log.info("入队消息ID: {}",correlationId);
            rabbitTemplate.convertAndSend(MQConfig.SERVICE_EXCHANGE, "#", objectMapper.writeValueAsString(serviceMessage), message -> {
                MessageProperties properties = message.getMessageProperties();
                properties.setDeliveryMode(MessageDeliveryMode.PERSISTENT);
                return message;
            });
        } catch (JsonProcessingException e) {
            throw new CustomException("System Error");
        }
    }

    @Override
    public void remindOtherServers(SystemMessage systemMessage) {
        MessageProperties messageProperties = new MessageProperties();
        messageProperties.setClusterId(serverId);
        try {
            Message mqMsg = new Message(objectMapper.writeValueAsString(systemMessage).getBytes(),messageProperties);
            //log.info("入队消息ID: {}",correlationId);
            rabbitTemplate.convertAndSend(MQConfig.SYSTEM_EXCHANGE, "#", mqMsg, message -> {
                MessageProperties properties = message.getMessageProperties();
                properties.setDeliveryMode(MessageDeliveryMode.PERSISTENT);
                return message;
            });
        } catch (JsonProcessingException e) {
            throw new CustomException("System Error");
        }
    }


}
