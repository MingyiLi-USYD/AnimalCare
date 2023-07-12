package usyd.mingyi.animalcare.controller;

import com.corundumstudio.socketio.SocketIOClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import usyd.mingyi.animalcare.common.R;
import usyd.mingyi.animalcare.component.ClientCache;
import usyd.mingyi.animalcare.dto.UserDto;
import usyd.mingyi.animalcare.pojo.User;
import usyd.mingyi.animalcare.socketEntity.ResponseMessage;
import usyd.mingyi.animalcare.service.FriendRequestService;
import usyd.mingyi.animalcare.service.FriendService;
import usyd.mingyi.animalcare.service.UserService;
import usyd.mingyi.animalcare.socketEntity.ServiceMessage;
import usyd.mingyi.animalcare.utils.BaseContext;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
public class FriendController {
    @Resource
    private ClientCache clientCache;
    @Resource
    private UserService userService;
    @Autowired
    FriendService friendService;
    @Autowired
    FriendRequestService friendRequestService;



    @GetMapping("/friends/status/{id}")
    @ResponseBody
    public R<Integer> getFriendshipStatus(@PathVariable("id") long toId) {
        long fromId = BaseContext.getCurrentId();
        if (fromId == toId) {
           return R.success(0);
        }
        return R.success(friendService.checkFriendshipStatus(fromId, toId));

    }

    @PostMapping ("/friendRequest/{id}")
    @ResponseBody
    public R<String> sendFriendRequest(@PathVariable("id") Long toId,@RequestParam("msg")String msg ) {
        Long currentId = BaseContext.getCurrentId();
        friendRequestService.sendRequest(currentId,toId,msg);
        Map<String, HashMap<UUID, SocketIOClient>> chatServer = clientCache.getChatServer();
        User me = userService.getBasicUserInfoById(currentId);
        HashMap<UUID, SocketIOClient> userClient = chatServer.get(String.valueOf(toId));
        ServiceMessage serviceMessage = new ServiceMessage(String.valueOf(currentId), System.currentTimeMillis(), String.valueOf(toId), 1);
        ResponseMessage<ServiceMessage> responseMessage = new ResponseMessage<>(2, serviceMessage,me);
        userClient.forEach((uuid, socketIOClient) -> {
            //向客户端推送消息
            //System.out.println("发消息中");
            socketIOClient.sendEvent("friendRequestEvent",responseMessage);
        });
        return R.success("request have been sent");
    }

    @GetMapping("/friendRequests")
    @ResponseBody
    public R<List<UserDto>> getRequestList() {
        Long currentId = BaseContext.getCurrentId();
        List<UserDto> allRequest = friendRequestService.getAllRequest(currentId);
        return R.success(allRequest);
    }

    @GetMapping("/friendRequest/{id}")
    @ResponseBody
    public R<User> approveFriendRequest(@PathVariable("id") long toId) {
        Long currentId = BaseContext.getCurrentId();
        friendRequestService.approveRequest(currentId,toId);
        return R.success(friendService.getFriendSync(toId));
    }

    @DeleteMapping("/friendRequest/{id}")
    @ResponseBody
    public R<String> rejectFriendRequest(@PathVariable("id") long toId) {
        Long currentId = BaseContext.getCurrentId();
        friendRequestService.rejectRequest(currentId,toId);
        return R.success("Successfully reject");
    }

    @GetMapping("/friends")
    @ResponseBody
    public R<List<User>> getFriendsList() {
        Long currentId = BaseContext.getCurrentId();
        List<User> allFriends = friendService.getAllFriends(currentId);
        return R.success(allFriends);
    }



    @DeleteMapping("/friends/{id}")
    @ResponseBody
    public R<String> deleteFriendFromList(@PathVariable("id") long toId) {
        Long currentId = BaseContext.getCurrentId();
        friendService.deleteUser(currentId,toId);
        return R.success("Successfully delete");
    }

}
