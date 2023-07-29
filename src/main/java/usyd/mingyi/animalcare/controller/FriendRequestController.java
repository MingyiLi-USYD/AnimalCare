package usyd.mingyi.animalcare.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import usyd.mingyi.animalcare.common.R;
import usyd.mingyi.animalcare.dto.FriendRequestDto;
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
        List<FriendRequestDto> allRequest = friendRequestService.getAllRequest(currentId);
        System.out.println(allRequest);
        return R.success(allRequest);
    }
}
