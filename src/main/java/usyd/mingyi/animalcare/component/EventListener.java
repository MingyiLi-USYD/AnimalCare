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
import usyd.mingyi.animalcare.socketEntity.ServiceMessageType;
import usyd.mingyi.animalcare.socketEntity.SystemMessage;
import usyd.mingyi.animalcare.utils.JWTUtils;

import javax.annotation.Resource;

@Component
@Slf4j
public class EventListener {
    @Resource
    private ClientCache clientCache;

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
        if(!validate(client,userId,token)){
            return;
        }
        beforeSaveToCache(userId);
        processSavingToCache(client,userId);
        afterSaveToCache(userId);

    }



    /**
     * 客户端断开
     *
     * @param client
     */
    @OnDisconnect
    public void onDisconnect(SocketIOClient client) {
        String reason = client.get("disconnectReason");
        //未成功连接
        if (!StringUtils.isEmpty(reason) && reason.equals(ClientCache.TOKEN_ISSUE)) {
            //本身就没有保持到cache里面 不需要删除
            log.info("token有问题");
            return;
        }

        if (!StringUtils.isEmpty(reason) && reason.equals(ClientCache.RE_LOGIN)) {
            //用户再次登录 检查到本地已经有了 在进入次方法前已经删除了之前的
            log.info("本地再次登录下线");
            return;
        }
        String userId = client.getHandshakeData().getSingleUrlParam("userId");

        if (!StringUtils.isEmpty(reason) && reason.equals(ClientCache.OTHER_LOGIN)) {

            log.info("异地再次登录下线");
            //需要从本地cache移除
            clientCache.deleteUserClient(userId);
            return;
        }
        //主动下线
        if (!StringUtils.isEmpty(userId)) {
            log.info("主动下线");
            ServiceMessage serviceMessage = new ServiceMessage(userId, System.currentTimeMillis(), null, ServiceMessageType.FRIEND_OFFLINE);
            realTimeService.remindFriends(serviceMessage);
            clientCache.deleteUserClient(userId);
        }
    }



    public boolean validate(SocketIOClient client,String userId,String token){
        if (token != null && JWTUtils.verifyInSocket(token)) {
            DecodedJWT tokenInfo = JWTUtils.getTokenInfo(token);
            Long tokenUserId = tokenInfo.getClaim("userId").asLong();
            if (!userId.equals(String.valueOf(tokenUserId))) {
                client.sendEvent("invalidTokenEvent", new ResponseMessage<>(0, "invalidToken", null));
                clientCache.disconnectClient(client,ClientCache.TOKEN_ISSUE);
                log.info("连接请求被拒绝");
                return false;
            }
        } else {
            client.sendEvent("invalidTokenEvent", new ResponseMessage<>(0, "invalidToken", null));
            clientCache.disconnectClient(client,ClientCache.TOKEN_ISSUE);
            log.info("连接请求被拒绝");
            return false;
        }
        return true;
    }

    public void beforeSaveToCache (String userId){
        log.info("在保存之前");
        //删除本地已经有的连接
        if(clientCache.hasUserClient(userId)){
            SocketIOClient userClient = clientCache.getUserClient(userId);
            clientCache.disconnectClient(userClient,ClientCache.RE_LOGIN);
            clientCache.deleteUserClient(userId);
        }
        //通知其他服务器让此用户下线
        realTimeService.remindOtherServers(new SystemMessage(userId,System.currentTimeMillis(),null,"ServerA"));
    }

    public void processSavingToCache(SocketIOClient client,String userId){
        clientCache.saveClient(userId,client);
    }

    public void afterSaveToCache(String userId){
        //通知好友上线
        ServiceMessage serviceMessage = new ServiceMessage(userId, System.currentTimeMillis(), null, ServiceMessageType.FRIEND_ONLINE);
        realTimeService.remindFriends(serviceMessage);
    }


}