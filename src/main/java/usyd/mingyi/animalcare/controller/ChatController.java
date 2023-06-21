package usyd.mingyi.animalcare.controller;

import com.corundumstudio.socketio.SocketIOClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import usyd.mingyi.animalcare.common.R;
import usyd.mingyi.animalcare.component.ClientCache;
import usyd.mingyi.animalcare.pojo.User;
import usyd.mingyi.animalcare.pojo.chat.RequestMessage;
import usyd.mingyi.animalcare.pojo.chat.ResponseMessage;
import usyd.mingyi.animalcare.service.FriendService;
import usyd.mingyi.animalcare.service.UserService;
import usyd.mingyi.animalcare.utils.BaseContext;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@Slf4j
public class ChatController {
    @Resource
    private ClientCache clientCache;

    @Resource
    private UserService userService;


    @PostMapping("/chat/message")
    public R<String> pushTuUser(@RequestBody RequestMessage message){
        Long currentId = BaseContext.getCurrentId();
        //还需要检查朋友的关系
        Map<String, HashMap<UUID, SocketIOClient>> chatServer = clientCache.getChatServer();
        User me = userService.getById(currentId);
        HashMap<UUID, SocketIOClient> userClient = chatServer.get(String.valueOf(message.getToId()));

        if (userClient==null||userClient.size()==0){
            return R.success("对方不在线 后面再发给他");
        }
        ResponseMessage responseMessage = new ResponseMessage(false, "CHAT", me, message.getMessage());
        userClient.forEach((uuid, socketIOClient) -> {
            //向客户端推送消息
            //System.out.println("发消息中");
            socketIOClient.sendEvent("responseMessage",responseMessage);
        });
        return R.success("成功发送");
    }
}
