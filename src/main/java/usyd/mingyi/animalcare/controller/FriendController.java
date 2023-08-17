package usyd.mingyi.animalcare.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import usyd.mingyi.animalcare.common.R;
import usyd.mingyi.animalcare.dto.FriendshipDto;
import usyd.mingyi.animalcare.service.FriendRequestService;
import usyd.mingyi.animalcare.service.FriendshipService;
import usyd.mingyi.animalcare.utils.BaseContext;

import java.util.List;

@RestController
public class FriendController {

    @Autowired
    FriendshipService friendService;

    @Autowired
    FriendRequestService friendRequestService;


    @GetMapping("/friends/status/{id}")
    @ResponseBody
    public R<Integer> getFriendshipStatus(@PathVariable("id") Long toId) {
        Long fromId = BaseContext.getCurrentId();
        if (fromId.equals(toId)) {
            return R.success(0);
        }
        return R.success(friendService.checkFriendshipStatus(fromId, toId));

    }


    @GetMapping("/friends")

    public R<List<FriendshipDto>> getFriendsList() {
        Long currentId = BaseContext.getCurrentId();
        List<FriendshipDto> allFriends = friendService.getAllFriends(currentId);
        return R.success(allFriends);
    }

    @PostMapping("/friends/byIds")
    public R<List<FriendshipDto>> getFriendsListByIds(@RequestBody Long[] ids) {
        Long currentId = BaseContext.getCurrentId();
        List<FriendshipDto> allFriends = friendService.getAllFriends(currentId,ids);
        return R.success(allFriends);
    }


    @DeleteMapping("/friends/{id}")
    @ResponseBody
    public R<String> deleteFriendFromList(@PathVariable("id") long toId) {
        Long currentId = BaseContext.getCurrentId();
        friendService.deleteUserSyncSocket(currentId, toId);
        return R.success("Successfully delete");
    }
}
