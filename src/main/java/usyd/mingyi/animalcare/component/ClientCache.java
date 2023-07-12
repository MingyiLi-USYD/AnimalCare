package usyd.mingyi.animalcare.component;

import com.alibaba.druid.util.StringUtils;
import com.corundumstudio.socketio.SocketIOClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * kcm
 */
@Component
@Slf4j
public class ClientCache {

    //本地缓存
    private static Map<String, HashMap<UUID, SocketIOClient>> chatServer=new ConcurrentHashMap<>();
    /**
     * 存入本地缓存
     * @param userId 用户ID
     * @param sessionId 页面sessionID
     * @param socketIOClient 页面对应的通道连接信息
     */
    public void saveClient(String userId, UUID sessionId,SocketIOClient socketIOClient){

        if(!StringUtils.isEmpty(userId)){
            HashMap<UUID, SocketIOClient> sessionIdClientCache=chatServer.get(userId);
            if(sessionIdClientCache==null){
                sessionIdClientCache = new HashMap<>();
            }
            sessionIdClientCache.put(sessionId,socketIOClient);
            chatServer.put(userId,sessionIdClientCache);
        }
    }
    /**
     * 根据用户ID获取所有通道信息
     * @param userId
     * @return
     */
    public HashMap<UUID, SocketIOClient> getUserClient(String userId){
        return chatServer.get(userId);
    }
    /**
     * 根据用户ID及页面sessionID删除页面链接信息
     * @param userId
     * @param sessionId
     */
    public void deleteSessionClient(String userId,UUID sessionId){
        if(chatServer.get(userId)!=null){
            chatServer.get(userId).remove(sessionId);
            log.info("移除用户: {}" ,sessionId);
            if(chatServer.get(userId).size()==0){
                chatServer.remove(userId);
            }
        }
    }

    public Map<String, HashMap<UUID, SocketIOClient>> getChatServer(){
        return chatServer;
    }
}
