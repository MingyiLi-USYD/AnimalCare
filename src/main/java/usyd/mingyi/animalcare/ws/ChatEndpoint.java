package usyd.mingyi.animalcare.ws;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import usyd.mingyi.animalcare.pojo.User;
import usyd.mingyi.animalcare.pojo.chat.ChatInfo;
import usyd.mingyi.animalcare.pojo.chat.RequestMessage;
import usyd.mingyi.animalcare.pojo.chat.ResponseMessage;
import usyd.mingyi.animalcare.service.FriendService;
import usyd.mingyi.animalcare.utils.BaseContext;

import javax.servlet.http.HttpSession;
import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@ServerEndpoint("/chat/{userId}")
@Component
@Slf4j
public class ChatEndpoint {

    private static FriendService friendService;

    private static ObjectMapper objectMapper;

    private static Map<Long,ChatEndpoint> onlineUsers = new ConcurrentHashMap<>();

    private Session session;


    @Autowired
    public void setApplicationContext(FriendService friendService,@Qualifier("customizeObjectMapper") ObjectMapper objectMapper){
        ChatEndpoint.objectMapper = objectMapper;
        ChatEndpoint.friendService = friendService;
    }

    @OnOpen
    public void onOpen(Session session, EndpointConfig config,@PathParam("userId") Long userId){
           this.session = session;
        log.info("当前登录用户的id是: {}",userId);
        onlineUsers.put(userId,this);
        broadcastAllUsers(userId);

    }
    private void broadcastAllUsers(Long id){

        List<User> allFriends = friendService.getAllFriends(id);
        log.info("我有{}个朋友",allFriends.size());
        log.info("系统现在有{}个人在线",onlineUsers.size());
        allFriends.forEach(friend->{
            if(onlineUsers.containsKey(friend.getId())){
                System.out.println("通知朋友");
                remindFriendOn(friend.getId(),id);
            }

        });


    }
    private void remindFriendOn(Long friend,Long me){
        try {
            ChatEndpoint chatEndpoint = onlineUsers.get(friend);
            log.info(objectMapper.writeValueAsString(new ResponseMessage(true,"ON",null,me)));
            chatEndpoint.session.getBasicRemote().sendText(objectMapper.writeValueAsString(new ResponseMessage(true,"ON",null,me)));
        }catch (Exception e){
            e.printStackTrace();
        }


    }

    @OnMessage
    public void onMessage(String message,Session session,@PathParam("userId") Long userId){

        try {
            RequestMessage requestMessage = objectMapper.readValue(message, RequestMessage.class);
            System.out.println(requestMessage);
            if(onlineUsers.containsKey(requestMessage.getToId())){
             ChatEndpoint chatEndpoint = onlineUsers.get(requestMessage.getToId());
                System.out.println(message);
             ResponseMessage responseMessage = new ResponseMessage(false, "CHAT", userId, requestMessage);
                System.out.println("开始回复");
                //JsonProperty.Access
                System.out.println(objectMapper.writeValueAsString(responseMessage));
             chatEndpoint.session.getBasicRemote().sendText(objectMapper.writeValueAsString(responseMessage));
            }else {
                //当前用户不在线
                log.info("当前用户不在线");
            }


        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }

    @OnClose
    public void onClose(Session session,@PathParam("userId") Integer userId){
        onlineUsers.remove(userId);

    }
}
