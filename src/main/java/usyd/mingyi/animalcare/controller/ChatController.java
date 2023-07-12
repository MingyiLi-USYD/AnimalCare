package usyd.mingyi.animalcare.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import usyd.mingyi.animalcare.common.R;
import usyd.mingyi.animalcare.pojo.User;
import usyd.mingyi.animalcare.socketEntity.ChatMessage;
import usyd.mingyi.animalcare.socketEntity.ResponseMessage;
import usyd.mingyi.animalcare.service.ChatService;
import usyd.mingyi.animalcare.service.UserService;
import usyd.mingyi.animalcare.utils.BaseContext;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@RestController
@Slf4j
public class ChatController {

    @Resource
    private UserService userService;
    @Resource
    private ChatService chatService;

    @PostMapping("/chat/message")
    public R<String> pushToUser(@RequestBody ChatMessage message){
        Long currentId = BaseContext.getCurrentId();
        //还需要检查朋友的关系
        User me = userService.getById(currentId);
         chatService.sendMsgToQueue(message);
        return R.success("成功发送");
    }

    @GetMapping("/chat/retrieve/{id}")
    public R<List<ChatMessage>> getMessage(@PathVariable("id") String toId){
        Long currentId = BaseContext.getCurrentId();
        CompletableFuture<List<ChatMessage>> future = chatService.retrieveDataFromFirebase(String.valueOf(currentId), toId);

        try {
            List<ChatMessage> responseMessages = future.get();
            return R.success(responseMessages);
        } catch (InterruptedException | ExecutionException e) {
            // 处理异常情况
            e.printStackTrace();
            return R.error("System error");
        }

    }


}
