package usyd.mingyi.animalcare.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import usyd.mingyi.animalcare.common.R;
import usyd.mingyi.animalcare.component.ClientCache;
import usyd.mingyi.animalcare.dto.FriendRequestDto;
import usyd.mingyi.animalcare.dto.FriendshipDto;
import usyd.mingyi.animalcare.dto.UserDto;
import usyd.mingyi.animalcare.pojo.Friendship;
import usyd.mingyi.animalcare.pojo.User;
import usyd.mingyi.animalcare.service.*;
import usyd.mingyi.animalcare.socketEntity.ServiceMessage;
import usyd.mingyi.animalcare.utils.BaseContext;

import javax.annotation.Resource;
import java.util.List;

@RestController
public class FriendController {

    @Autowired
    FriendshipService friendService;
    @Autowired
    FriendServiceSync friendServiceSync;
    @Autowired
    FriendRequestService friendRequestService;

    @Autowired
    FriendRequestServiceSync friendRequestServiceSync;




    @GetMapping("/friends/status/{id}")
    @ResponseBody
    public R<Integer> getFriendshipStatus(@PathVariable("id") Long toId) {
        Long fromId = BaseContext.getCurrentId();
        if (fromId .equals(toId)) {
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



    @GetMapping("/friendRequest/{id}")
    @ResponseBody
    public R<FriendshipDto> approveFriendRequest(@PathVariable("id") long toId) {
        Long currentId = BaseContext.getCurrentId();
       return R.success( friendRequestServiceSync.approveRequestSync(currentId, toId));
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
    public R<List<FriendshipDto>> getFriendsList() {
        Long currentId = BaseContext.getCurrentId();
        List<FriendshipDto> allFriends = friendService.getAllFriends(currentId);
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
