package usyd.mingyi.animalcare.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import usyd.mingyi.animalcare.common.R;
import usyd.mingyi.animalcare.dto.FriendRequestDto;
import usyd.mingyi.animalcare.dto.FriendshipDto;
import usyd.mingyi.animalcare.service.FriendRequestService;
import usyd.mingyi.animalcare.utils.BaseContext;

import java.util.List;

@RestController
public class FriendRequestController {
    @Autowired
    FriendRequestService friendRequestService;

    @GetMapping("/friendRequests")
    public R<List<FriendRequestDto>> getAllRequests(){
        Long currentId = BaseContext.getCurrentId();
        List<FriendRequestDto> allRequest = friendRequestService.getAllRequestsAndMarkRead(currentId);
        return R.success(allRequest);
    }
    @PostMapping ("/friendRequests/byIds")
    public R<List<FriendRequestDto>> getAllRequestsByIds(@RequestBody Long[] ids){
        Long currentId = BaseContext.getCurrentId();
        List<FriendRequestDto> allRequest = friendRequestService.getAllRequestsAndMarkRead(currentId,ids);
        return R.success(allRequest);
    }

    @PostMapping("/friendRequest/{id}")
    @ResponseBody
    public R<String> sendFriendRequest(@PathVariable("id") Long toId, @RequestParam("msg") String msg) {
        Long currentId = BaseContext.getCurrentId();
        friendRequestService.sendRequestSyncSocket(currentId, toId, msg);
        return R.success("request have been sent");
    }



    @GetMapping("/friendRequest/{id}")
    @ResponseBody
    public R<FriendshipDto> approveFriendRequest(@PathVariable("id") long toId) {
        Long currentId = BaseContext.getCurrentId();
        return R.success( friendRequestService.approveRequestAndGetSyncSocket(currentId, toId));
    }

    @DeleteMapping("/friendRequest/{id}")
    @ResponseBody
    public R<String> rejectFriendRequest(@PathVariable("id") long toId) {
        Long currentId = BaseContext.getCurrentId();
        friendRequestService.rejectRequestSyncSocket(currentId, toId);
        return R.success("Successfully reject");
    }

}
