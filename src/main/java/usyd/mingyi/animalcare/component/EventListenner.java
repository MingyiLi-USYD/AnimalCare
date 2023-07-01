package usyd.mingyi.animalcare.component;

import com.alibaba.druid.util.StringUtils;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.annotation.OnConnect;
import com.corundumstudio.socketio.annotation.OnDisconnect;
import com.corundumstudio.socketio.annotation.OnEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import usyd.mingyi.animalcare.pojo.User;
import usyd.mingyi.animalcare.pojo.chat.RequestMessage;
import usyd.mingyi.animalcare.pojo.chat.ResponseMessage;
import usyd.mingyi.animalcare.service.FriendService;
import usyd.mingyi.animalcare.utils.JWTUtils;

import javax.annotation.Resource;
import java.util.*;

@Component
@Slf4j
public class EventListenner {
    @Resource
    private ClientCache clientCache;
    private final  FriendService friendService;
    private final ObjectMapper objectMapper;

    public final static String ON = "ON";
    public final static String OFF = "OFF";

    public final static String TOKEN_ISSUE = "TOKEN_ISSUE";

    @Autowired
    public EventListenner(FriendService friendService,ObjectMapper objectMapper) {
        this.friendService=friendService;
        this.objectMapper=objectMapper;
    }
    /**
     * 客户端连接
     *
     * @param client
     */
    @OnConnect
    public void onConnect(SocketIOClient client) {
        String userId = client.getHandshakeData().getSingleUrlParam("userId");
        String token = client.getHandshakeData().getSingleUrlParam("token");
         if(token!=null&&JWTUtils.verifyInSocket(token)){
             DecodedJWT tokenInfo = JWTUtils.getTokenInfo(token);
             Long tokenUserId = tokenInfo.getClaim("userId").asLong();
             if(!userId.equals(String.valueOf(tokenUserId))){
                 System.out.println(userId);
                 System.out.println(tokenUserId);

                 client.sendEvent("invalidTokenEvent", new ResponseMessage(true, "Invalid token", null, null,"tokenId is not equal userId"));
                 disconnectClient(client,TOKEN_ISSUE);
                 log.info("连接请求被拒绝");
                 return;
             }
             UUID sessionId = client.getSessionId();

             clientCache.saveClient(userId, sessionId, client);
             //broadcastAllUsers(Long.valueOf(userId),ON);
                 //log.info("成功建立连接");
             log.info("登录用户: {}",String.valueOf(sessionId));
         }else {
             client.sendEvent("invalidTokenEvent", new ResponseMessage(true, "Invalid token", null,null, "invalid token"));
             disconnectClient(client,TOKEN_ISSUE);
             log.info("连接请求被拒绝");
         }



    }

    /**
     * 客户端断开
     *
     * @param client
     */
    @OnDisconnect
    public void onDisconnect(SocketIOClient client) {
        String reason = client.get("disconnectReason");
        if(!StringUtils.isEmpty(reason)&&reason.equals(TOKEN_ISSUE)){
            return;
        }
        String userId = client.getHandshakeData().getSingleUrlParam("userId");
        if (!StringUtils.isEmpty(userId)) {
            //broadcastAllUsers(Long.valueOf(userId),OFF);
            clientCache.deleteSessionClient(userId, client.getSessionId());
        }


    }

    private void broadcastAllUsers(Long id,String type){

        List<User> allFriends = friendService.getAllFriends(id);
        if(allFriends==null) return;
        log.info("我有{}个朋友",allFriends.size());
        log.info("系统现在有{}个人在线",clientCache.getChatServer().size());
        Map<String, HashMap<UUID, SocketIOClient>> concurrentHashMap = clientCache.getChatServer();
        allFriends.forEach(friend->{
            if(concurrentHashMap.containsKey(String.valueOf(friend.getId()))){
                if (type.equals(ON)) {
                    remindFriend(friend.getId(), id,ON);
                } else if(type.equals(OFF)) {
                    remindFriend(friend.getId(), id,OFF);
                }
            }

        });
    }

    private void remindFriend(Long friend,Long me,String type)  {
        log.info("我{}给我朋友{}发消息",me,friend);
        HashMap<UUID, SocketIOClient> userClient = clientCache.getUserClient(String.valueOf(friend));
        Iterator<Map.Entry<UUID, SocketIOClient>> iterator = userClient.entrySet().iterator();
        while(iterator.hasNext()){
            log.info("正在发消息");
            Map.Entry<UUID, SocketIOClient> next = iterator.next();
            next.getValue().sendEvent("friendEvent",new ResponseMessage(true,type,null,null,me));
        }
    }

    public void disconnectClient(SocketIOClient client, String reason) {
        // Store the reason for disconnection
        client.set("disconnectReason", reason);
        // Disconnect the client
        client.disconnect();
    }


}