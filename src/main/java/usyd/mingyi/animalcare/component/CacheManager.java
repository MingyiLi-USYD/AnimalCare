package usyd.mingyi.animalcare.component;

import com.corundumstudio.socketio.SocketIOClient;

import java.util.Map;

public interface CacheManager {
     void saveUserClient(String userId, SocketIOClient socketIOClient);
     SocketIOClient getUserClient(String userId);

     Boolean hasUserClient(String userId);

     void deleteUserClient(String userId);

     Map<String, SocketIOClient> getChatServer();

     void disconnectClient(SocketIOClient client, String reason);
}
