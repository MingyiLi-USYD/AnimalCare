package usyd.mingyi.animalcare.component;

import com.alibaba.druid.util.StringUtils;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.annotation.OnConnect;
import com.corundumstudio.socketio.annotation.OnDisconnect;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import usyd.mingyi.animalcare.service.RealTimeService;
import usyd.mingyi.animalcare.socketEntity.ResponseMessage;
import usyd.mingyi.animalcare.socketEntity.ServiceMessage;
import usyd.mingyi.animalcare.utils.JWTUtils;

import javax.annotation.Resource;
import java.util.*;

@Component
@Slf4j
public class EventListener {
    @Resource
    private ClientCache clientCache;
    public final static String TOKEN_ISSUE = "TOKEN_ISSUE";
    public final RealTimeService realTimeService;

    @Autowired
    public EventListener(RealTimeService realTimeService) {
        this.realTimeService = realTimeService;
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
                 client.sendEvent("invalidTokenEvent", new ResponseMessage<>(0, "invalidToken",null));
                 disconnectClient(client,TOKEN_ISSUE);
                 log.info("连接请求被拒绝");
                 return;
             }
             UUID sessionId = client.getSessionId();
             clientCache.saveClient(userId, sessionId, client);
             ServiceMessage serviceMessage = new ServiceMessage(userId,System.currentTimeMillis(),null,3);
             realTimeService.remindFriends(serviceMessage);
             log.info("登录用户: {}",String.valueOf(sessionId));
         }else {
             client.sendEvent("invalidTokenEvent", new ResponseMessage<>(0, "invalidToken",null));
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
            ServiceMessage serviceMessage = new ServiceMessage(userId,System.currentTimeMillis(),null,4);
            realTimeService.remindFriends(serviceMessage);
            clientCache.deleteSessionClient(userId, client.getSessionId());
        }

    }



    public void disconnectClient(SocketIOClient client, String reason) {
        // Store the reason for disconnection
        client.set("disconnectReason", reason);
        // Disconnect the client
        client.disconnect();
    }


}