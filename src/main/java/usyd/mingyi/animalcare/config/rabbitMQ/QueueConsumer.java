package usyd.mingyi.animalcare.config.rabbitMQ;

import com.corundumstudio.socketio.SocketIOClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import usyd.mingyi.animalcare.component.CacheManager;
import usyd.mingyi.animalcare.component.ClientCache;
import usyd.mingyi.animalcare.dto.FriendshipDto;
import usyd.mingyi.animalcare.pojo.User;
import usyd.mingyi.animalcare.service.FriendshipService;
import usyd.mingyi.animalcare.service.UserService;
import usyd.mingyi.animalcare.socketEntity.ChatMessage;
import usyd.mingyi.animalcare.socketEntity.ResponseMessage;
import usyd.mingyi.animalcare.socketEntity.ServiceMessage;
import usyd.mingyi.animalcare.socketEntity.SystemMessage;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

import static usyd.mingyi.animalcare.socketEntity.ServiceMessageType.FRIEND_OFFLINE;
import static usyd.mingyi.animalcare.socketEntity.ServiceMessageType.FRIEND_ONLINE;


@Component
@Slf4j
public class QueueConsumer {
    @Value("${serverId}")
    private String serverId;
    @Autowired
    @Qualifier("redisDecorator")
    private CacheManager clientCache;
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private FriendshipService friendshipService;

    @Autowired
    private UserService userService;


    @RabbitListener(queues = MQConfig.MESSAGE + "${serverId}")
    public void receiveD(Message message, Channel channel) {

        try {
            String receivedBytes = new String(message.getBody());
            ChatMessage chatMessage = objectMapper.readValue(receivedBytes, ChatMessage.class);
            User basicUserInfoById = userService.getBasicUserInfoById(Long.valueOf(chatMessage.getFromId()));
            Map<String, SocketIOClient> chatServer = clientCache.getChatServer();

            ResponseMessage<ChatMessage> res = new ResponseMessage<>(1, chatMessage, basicUserInfoById);
            if (!chatServer.containsKey(chatMessage.getToId())) {
                log.info("not found in this server");
            } else {
                SocketIOClient userClient = chatServer.get(chatMessage.getToId());
                userClient.sendEvent("responseMessage", res);
            }
        } catch (Exception e) {
            //requeue
            e.printStackTrace();
        }

    }

    @RabbitListener(queues = MQConfig.SERVICE + "${serverId}")
    public void receiveServiceMessage(Message message, Channel channel) {

        try {
            String receivedBytes = new String(message.getBody());
            ServiceMessage serviceMessage = objectMapper.readValue(receivedBytes, ServiceMessage.class);
            if (serviceMessage.getType() == FRIEND_ONLINE ||
                    serviceMessage.getType() == FRIEND_OFFLINE) {
                syncOnAndOffToClient(serviceMessage);
            } else {
                syncFriendOperationToClient(serviceMessage);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @RabbitListener(queues = MQConfig.SYSTEM + "${serverId}")
    public void receiveSystemMessage(Message message, Channel channel) {
        MessageProperties messageProperties = message.getMessageProperties();
        String clusterId = messageProperties.getClusterId();
        if (clusterId.equals(serverId)) {
            log.info("收到自己服务器的通知");
            return;
        }
        try {
            String receivedBytes = new String(message.getBody());
            SystemMessage systemMessage = objectMapper.readValue(receivedBytes, SystemMessage.class);
            log.info("收到其他服务器的通知");
           // clientCache.receiveDisconnectMsg(systemMessage.getFromId());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void syncOnAndOffToClient(ServiceMessage serviceMessage) {
        User basicUserInfoById = userService.getBasicUserInfoById(Long.valueOf(serviceMessage.getFromId()));
        Map<String, SocketIOClient> chatServer = clientCache.getChatServer();
        Long id = basicUserInfoById.getUserId();
        //找到所有好友
        List<User> allFriends = friendshipService.getAllFriends(id).stream().map(FriendshipDto::getFriendInfo).toList();

        ResponseMessage<ServiceMessage> res = new ResponseMessage<>(2, serviceMessage, basicUserInfoById);
        allFriends.forEach(friend -> {
            String friendId = String.valueOf(friend.getUserId());
            if (chatServer.containsKey(friendId)) {
                SocketIOClient userClient = chatServer.get(friendId);
                res.getMessage().setToId(String.valueOf(friend.getUserId()));
                userClient.sendEvent("friendEvent", res);
            }

        });
    }

    public void syncFriendOperationToClient(ServiceMessage serviceMessage) {
        User user = userService.getBasicUserInfoById(Long.valueOf(serviceMessage.getFromId()));
        if (user == null) {
            return;
        }
        socketSendMsg(serviceMessage, user);
    }

    public void socketSendMsg(ServiceMessage serviceMessage, User user) {
        Map<String, SocketIOClient> chatServer = clientCache.getChatServer();
        ResponseMessage<ServiceMessage> res = new ResponseMessage<>(2, serviceMessage, user);
        if (chatServer.containsKey(serviceMessage.getToId())) {
            SocketIOClient userClient = chatServer.get(serviceMessage.getToId());
            userClient.sendEvent("friendEvent", res);
        }
    }
}
