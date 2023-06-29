package usyd.mingyi.animalcare.controller;

import com.corundumstudio.socketio.SocketIOClient;
import com.google.firebase.database.DatabaseReference;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import usyd.mingyi.animalcare.common.R;
import usyd.mingyi.animalcare.component.ClientCache;
import usyd.mingyi.animalcare.pojo.User;
import usyd.mingyi.animalcare.pojo.chat.Message;
import usyd.mingyi.animalcare.pojo.chat.RequestMessage;
import usyd.mingyi.animalcare.pojo.chat.ResponseMessage;
import usyd.mingyi.animalcare.service.ChatService;
import usyd.mingyi.animalcare.service.UserService;
import usyd.mingyi.animalcare.utils.BaseContext;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@RestController
@Slf4j
public class ChatController {
    @Resource
    private ClientCache clientCache;

    @Resource
    private UserService userService;

    @Resource
    private ChatService chatService;

    @PostMapping("/chat/message")
    public R<String> pushTuUser(@RequestBody RequestMessage message){
        Long currentId = BaseContext.getCurrentId();
        //还需要检查朋友的关系
        Map<String, HashMap<UUID, SocketIOClient>> chatServer = clientCache.getChatServer();
        User me = userService.getById(currentId);
        HashMap<UUID, SocketIOClient> userClient = chatServer.get(String.valueOf(message.getToId()));
        ResponseMessage responseMessage = new ResponseMessage(false, "CHAT", me, message.getMessage());
        chatService.sendMsgToFirebase(String.valueOf(currentId),message.getToId(),responseMessage);
        if (userClient==null||userClient.size()==0){
            return R.success("对方不在线 后面再发给他");
        }
        userClient.forEach((uuid, socketIOClient) -> {
            //向客户端推送消息
            //System.out.println("发消息中");
            socketIOClient.sendEvent("responseMessage",responseMessage);
        });
        return R.success("成功发送");
    }

    @GetMapping("/chat/retrieve/{id}")
    public R<List<Message>> getMessage(@PathVariable("id") String toId){
        Long currentId = BaseContext.getCurrentId();
        CompletableFuture<List<Message>> future = chatService.retrieveDataFromFirebase(String.valueOf(currentId), toId);

        try {
            List<Message> responseMessages = future.get();
            return R.success(responseMessages);
        } catch (InterruptedException | ExecutionException e) {
            // 处理异常情况
            e.printStackTrace();
            return R.error("System error");
        }

    }
}
