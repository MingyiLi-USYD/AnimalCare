package usyd.mingyi.animalcare.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import usyd.mingyi.animalcare.common.R;
import usyd.mingyi.animalcare.component.ClientCache;
import usyd.mingyi.animalcare.dto.UserDto;
import usyd.mingyi.animalcare.pojo.User;
import usyd.mingyi.animalcare.service.*;
import usyd.mingyi.animalcare.socketEntity.ServiceMessage;
import usyd.mingyi.animalcare.utils.BaseContext;

import javax.annotation.Resource;
import java.util.List;

@RestController
public class FriendController {

    @Autowired
    FriendService friendService;
    @Autowired
    FriendServiceSync friendServiceSync;
    @Autowired
    FriendRequestService friendRequestService;

    @Autowired
    FriendRequestServiceSync friendRequestServiceSync;




    @GetMapping("/friends/status/{id}")
    @ResponseBody
    public R<Integer> getFriendshipStatus(@PathVariable("id") long toId) {
        long fromId = BaseContext.getCurrentId();
        if (fromId == toId) {
            return R.success(0);
        }
        return R.success(friendService.checkFriendshipStatus(fromId, toId));

    }

    @PostMapping("/friendRequest/{id}")
    @ResponseBody
    public R<String> sendFriendRequest(@PathVariable("id") Long toId, @RequestParam("msg") String msg) {
        Long currentId = BaseContext.getCurrentId();
        friendRequestServiceSync.sendRequestSync(currentId, toId, msg);
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
        friendRequestServiceSync.approveRequestSync(currentId, toId);
        return R.success(friendService.getFriendSync(toId));
    }

    @DeleteMapping("/friendRequest/{id}")
    @ResponseBody
    public R<String> rejectFriendRequest(@PathVariable("id") long toId) {
        Long currentId = BaseContext.getCurrentId();
        friendRequestServiceSync.rejectRequestSync(currentId, toId);
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
        friendServiceSync.deleteUserSync(currentId, toId);
        return R.success("Successfully delete");
    }

}
