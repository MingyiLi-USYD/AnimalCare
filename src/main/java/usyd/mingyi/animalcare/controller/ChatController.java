package usyd.mingyi.animalcare.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import usyd.mingyi.animalcare.common.CustomException;
import usyd.mingyi.animalcare.common.R;
import usyd.mingyi.animalcare.mongodb.entity.CloudMessage;
import usyd.mingyi.animalcare.pojo.User;
import usyd.mingyi.animalcare.service.ChatService;
import usyd.mingyi.animalcare.service.UserService;
import usyd.mingyi.animalcare.socketEntity.ChatMessage;
import usyd.mingyi.animalcare.utils.BaseContext;
import java.util.List;
import java.util.Map;

@RestController
@Slf4j
public class ChatController {


    private final UserService userService;
    private final ChatService chatService;

    @Autowired
    public ChatController(UserService userService, ChatService chatService) {
        this.userService = userService;
        this.chatService = chatService;
    }



    @PostMapping("/chat/message")
    public R<String> pushToUser(@RequestBody ChatMessage message){
        Long currentId = BaseContext.getCurrentId();
        if(!message.getFromId().equals(String.valueOf(currentId))){
            throw new CustomException("no right");
        }
        //还需要检查朋友的关系
        User me = userService.getById(currentId);
        chatService.sendMsgToQueue(message);
        chatService.saveMsgInCloud(message);
        return R.success("成功发送");
    }

    @GetMapping("/chat/retrieve/{id}")
    public R<CloudMessage> getMessages(@PathVariable("id") Long toId){
        Long currentId = BaseContext.getCurrentId();
       return R.success(chatService.retrieveDataFromMongoDB(currentId,toId)) ;

    }

    @GetMapping("/chat/retrieve/all")
    public R<List<CloudMessage>> getAllMessages(){
        Long currentId = BaseContext.getCurrentId();
        return  R.success(chatService.retrieveAllDataFromMongoDB(String.valueOf(currentId)));
    }
    @PostMapping("/chat/retrieve/partly")
    public R<List<CloudMessage>> getPartly(@RequestBody Map<String,Long> localStorage){
        Long currentId = BaseContext.getCurrentId();
       if(!localStorage.isEmpty()){
          return R.success(chatService.retrievePartlyDataFromMongoDB(String.valueOf(BaseContext.getCurrentId()),localStorage));
       }
        return null;
    }

}
