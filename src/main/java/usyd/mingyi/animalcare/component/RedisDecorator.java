package usyd.mingyi.animalcare.component;

import com.corundumstudio.socketio.SocketIOClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import usyd.mingyi.animalcare.common.CustomException;


@Component("redisDecorator")
public class RedisDecorator extends AbstractCacheManager{
    @Autowired
    RedisTemplate<String, Object> redisTemplate;

    @Value("${serverId}")
    private String serverId;

    @Autowired
    public RedisDecorator(@Qualifier("clientCache") CacheManager clientCache) {
        super(clientCache);
    }

    @Override
    public void saveUserClient(String userId, SocketIOClient socketIOClient) {
        BoundHashOperations<String, String, Object> hashOperations
                = redisTemplate.boundHashOps("socketIOLocation");
        if (hashOperations==null){//虽然不可能为空
            throw new CustomException("服务器异常");
        }
         if(hashOperations.hasKey(userId)){
             //检查当前是否在线 如果在线获取是在哪个服务器上连接的
             // 如果非此服务器 需要通知服务器移除这个socket
             String serverId = (String) hashOperations.get(userId);
             if(serverId.equals(this.serverId)){
                 //证明在此服务器上 执行下线操作
                 //先通知下线
                 SocketIOClient userClient = this.getUserClient(userId);
                 this.disconnectClient(userClient,ClientCache.RE_LOGIN);
                 //从map中移除
                 this.deleteUserClient(userId);
             }else {
                 //证明不在此服务器上需要通知其他服务器执行下线操作
                 //模拟通知
             }
         }
        //把此socketIO再次放入
        hashOperations.put(userId,this.serverId);
        super.saveUserClient(userId, socketIOClient);
    }

    @Override
    public void deleteUserClient(String userId) {
        super.deleteUserClient(userId);
    }
}
