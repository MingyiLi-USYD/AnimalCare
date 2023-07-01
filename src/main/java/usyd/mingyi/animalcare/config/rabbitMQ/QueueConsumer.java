package usyd.mingyi.animalcare.config.rabbitMQ;

import com.corundumstudio.socketio.SocketIOClient;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import usyd.mingyi.animalcare.component.ClientCache;

import usyd.mingyi.animalcare.mapper.ChatMapper;
import usyd.mingyi.animalcare.pojo.chat.ResponseMessage;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;



@Component
@Slf4j
public class QueueConsumer {
    @Resource
    private ClientCache clientCache;
    @Resource
    private ObjectMapper objectMapper;
    @Resource
    private ChatMapper chatMapper;


    @RabbitListener(queues = MQConfig.QUEUE_A)
    public void receiveD(Message message, Channel channel)  {

          try {
              String receivedBytes  = new String(message.getBody());
              ResponseMessage<usyd.mingyi.animalcare.pojo.chat.Message> responseMessage = objectMapper.readValue(receivedBytes, new TypeReference<ResponseMessage<usyd.mingyi.animalcare.pojo.chat.Message>>() {});
              Map<String, HashMap<UUID, SocketIOClient>> chatServer = clientCache.getChatServer();
              HashMap<UUID, SocketIOClient> userClient = chatServer.get(responseMessage.getToUser());
              chatMapper.sendMsgToFirebase(String.valueOf(responseMessage.getFromUser().getId()), responseMessage.getToUser(),responseMessage);
              if (userClient==null||userClient.size()==0){
                   log.info("not found in this server");
              }else {
                  userClient.forEach((uuid, socketIOClient) -> {
                      socketIOClient.sendEvent("responseMessage",responseMessage);
                  });
              }
          }catch (Exception e){
              e.printStackTrace();
          }

    }
}
