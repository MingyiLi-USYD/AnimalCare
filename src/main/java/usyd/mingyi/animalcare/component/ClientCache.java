package usyd.mingyi.animalcare.component;

import com.corundumstudio.socketio.SocketIOClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component("clientCache")
@Slf4j
public class ClientCache implements CacheManager {
    public final static String TOKEN_ISSUE = "TOKEN_ISSUE";
    public final static String RE_LOGIN = "RE_LOGIN";
    public final static String OTHER_LOGIN = "OTHER_LOGIN";
    //本地缓存
    private static final Map<String, SocketIOClient> chatServer = new ConcurrentHashMap<>();
    public void saveUserClient(String userId, SocketIOClient socketIOClient) {
        chatServer.put(userId, socketIOClient);
    }
    public SocketIOClient getUserClient(String userId) {
        return chatServer.get(userId);
    }

    public Boolean hasUserClient(String userId) {
        return chatServer.containsKey(userId);
    }

    public void deleteUserClient(String userId) {
        chatServer.remove(userId);
    }

    public Map<String, SocketIOClient> getChatServer() {
        return chatServer;
    }

    public void receiveDisconnectMsg(String id) {
        if (chatServer.containsKey(id)) {
            SocketIOClient socketIOClient = chatServer.get(id);
            disconnectClient(socketIOClient, OTHER_LOGIN);
        }
    }
    public void disconnectClient(SocketIOClient client, String reason) {
        // Store the reason for disconnection
        client.set("disconnectReason", reason);
        // Disconnect the client
        client.disconnect();
    }

}
