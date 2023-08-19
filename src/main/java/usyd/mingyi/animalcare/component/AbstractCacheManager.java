package usyd.mingyi.animalcare.component;

import com.corundumstudio.socketio.SocketIOClient;

import java.util.Map;

public abstract class AbstractCacheManager implements CacheManager {
    CacheManager clientCache;

    public AbstractCacheManager(CacheManager clientCache) {
        this.clientCache = clientCache;
    }

    @Override
    public void saveUserClient(String userId, SocketIOClient socketIOClient) {
         clientCache.saveUserClient(userId,socketIOClient);
    }

    @Override
    public SocketIOClient getUserClient(String userId) {
       return clientCache.getUserClient(userId);
    }

    @Override
    public Boolean hasUserClient(String userId) {
        return clientCache.hasUserClient(userId);
    }

    @Override
    public void deleteUserClient(String userId) {
          if(clientCache.hasUserClient(userId)){
              clientCache.deleteUserClient(userId);
          }
    }

    @Override
    public Map<String, SocketIOClient> getChatServer() {
        return clientCache.getChatServer();
    }

    @Override
    public void disconnectClient(SocketIOClient client, String reason) {
          if(client!=null){
              clientCache.disconnectClient(client,reason);
          }
    }
}
