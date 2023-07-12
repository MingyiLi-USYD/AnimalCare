package usyd.mingyi.animalcare.config.rabbitMQ;

import com.corundumstudio.socketio.SocketIOClient;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import usyd.mingyi.animalcare.component.ClientCache;

import usyd.mingyi.animalcare.dto.UserDto;
import usyd.mingyi.animalcare.mapper.ChatMapper;
import usyd.mingyi.animalcare.pojo.User;
import usyd.mingyi.animalcare.service.FriendRequestService;
import usyd.mingyi.animalcare.service.FriendService;
import usyd.mingyi.animalcare.service.UserService;
import usyd.mingyi.animalcare.socketEntity.ChatMessage;
import usyd.mingyi.animalcare.socketEntity.ResponseMessage;
import usyd.mingyi.animalcare.socketEntity.ServiceMessage;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
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
    private FriendService friendService;

    @Resource
    private UserService userService;

    @Resource
    private FriendRequestService friendRequestService;
    @Resource
    private ChatMapper chatMapper;

    @RabbitListener(queues = "QA")
    public void receiveD(Message message, Channel channel)  {

          try {
              String receivedBytes  = new String(message.getBody());
              ChatMessage chatMessage = objectMapper.readValue(receivedBytes, ChatMessage.class);
              User basicUserInfoById = userService.getBasicUserInfoById(Long.valueOf(chatMessage.getFromId()));
              Map<String, HashMap<UUID, SocketIOClient>> chatServer = clientCache.getChatServer();
              HashMap<UUID, SocketIOClient> userClient = chatServer.get(chatMessage.getToId());
              ResponseMessage<ChatMessage> res = new ResponseMessage<>(1,chatMessage,basicUserInfoById);
              //chatMapper.sendMsgToFirebase(String.valueOf(responseMessage.getFromUser().getId()), responseMessage.getToUser(),responseMessage);
              if (userClient==null||userClient.size()==0){
                   log.info("not found in this server");
              }else {
                  userClient.forEach((uuid, socketIOClient) -> {
                      socketIOClient.sendEvent("responseMessage",res);
                  });
              }
          }catch (Exception e){
              //requeue
              e.printStackTrace();
          }

    }
    @RabbitListener(queues = "QC")
    public void receiveServiceMessage(Message message, Channel channel)  {

        try {
            String receivedBytes  = new String(message.getBody());
           ServiceMessage serviceMessage = objectMapper.readValue(receivedBytes,ServiceMessage.class);
           if(serviceMessage.getType().equals(1)){
               syncFriendRequestToClient(serviceMessage);
           }else {
               syncToClient(serviceMessage);
           }

        }catch (Exception e){
            //requeue
            e.printStackTrace();
        }

    }

    public void syncToClient(ServiceMessage serviceMessage){
        User basicUserInfoById = userService.getBasicUserInfoById(Long.valueOf(serviceMessage.getFromId()));
        Map<String, HashMap<UUID, SocketIOClient>> chatServer = clientCache.getChatServer();
        Long id = basicUserInfoById.getId();
        //找到所有好友
        List<User> allFriends = friendService.getAllFriends(id);
        ResponseMessage<ServiceMessage> res = new ResponseMessage<>(2,serviceMessage,basicUserInfoById);
        log.info("现在正在被QC处理中");
        System.out.println(res);
        allFriends.forEach(friend->{

            HashMap<UUID, SocketIOClient> userClient = chatServer.get(String.valueOf(friend.getId()));
            if(userClient!=null){
                userClient.forEach((uuid, socketIOClient) -> {
                    res.getMessage().setToId(String.valueOf(friend.getId()));
                    socketIOClient.sendEvent("friendEvent",res);
                });
            }

        });
    }

    public void syncFriendRequestToClient(ServiceMessage serviceMessage){
        UserDto userDto = friendRequestService.getRequestById(Long.valueOf(serviceMessage.getToId()), Long.valueOf(serviceMessage.getFromId()));
        if(userDto==null)return;
        Long id = userDto.getId();
        Map<String, HashMap<UUID, SocketIOClient>> chatServer = clientCache.getChatServer();

        //找到所有好友
        List<User> allFriends = friendService.getAllFriends(id);
        ResponseMessage<ServiceMessage> res = new ResponseMessage<>(2,serviceMessage,userDto);
        log.info("现在正在被QC处理中");
        System.out.println(res);
        allFriends.forEach(friend->{

            HashMap<UUID, SocketIOClient> userClient = chatServer.get(String.valueOf(friend.getId()));
            if(userClient!=null){
                userClient.forEach((uuid, socketIOClient) -> {
                    res.getMessage().setToId(String.valueOf(friend.getId()));
                    socketIOClient.sendEvent("friendEvent",res);
                });
            }

        });
    }
}
