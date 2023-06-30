package usyd.mingyi.animalcare.config;

import com.corundumstudio.socketio.SocketIOClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import usyd.mingyi.animalcare.component.ClientCache;
import usyd.mingyi.animalcare.pojo.chat.RequestMessage;
import usyd.mingyi.animalcare.pojo.chat.ResponseMessage;

import javax.annotation.Resource;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
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

    @RabbitListener(queues = MQConfig.QUEUE_A)
    public void receiveD(Message message, Channel channel)  {
        System.out.println("收到消息");
          try {
              String receivedBytes  = new String(message.getBody());
              ResponseMessage responseMessage = objectMapper.readValue(receivedBytes, ResponseMessage.class);
              Map<String, HashMap<UUID, SocketIOClient>> chatServer = clientCache.getChatServer();
              RequestMessage requestMessage =(RequestMessage) responseMessage.getMessage();
              System.out.println(requestMessage);
              HashMap<UUID, SocketIOClient> userClient = chatServer.get(String.valueOf(requestMessage.getToId()));
              //chatService.sendMsgToFirebase(String.valueOf(currentId),message.getToId(),responseMessage);
              if (userClient==null||userClient.size()==0){
                  System.out.println("没有这个用户");
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
