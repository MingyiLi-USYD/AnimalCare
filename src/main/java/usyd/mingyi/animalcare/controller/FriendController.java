package usyd.mingyi.animalcare.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import usyd.mingyi.animalcare.common.CustomException;
import usyd.mingyi.animalcare.common.R;
import usyd.mingyi.animalcare.pojo.User;
import usyd.mingyi.animalcare.service.FriendService;
import usyd.mingyi.animalcare.utils.BaseContext;
import usyd.mingyi.animalcare.utils.ResultData;

import javax.servlet.http.HttpSession;
import java.util.List;

@RestController
public class FriendController {
    @Autowired
    FriendService friendService;
    @GetMapping("/friends/status/{id}")
    @ResponseBody
    public R<Integer> getFriendshipStatus(@PathVariable("id") long toId) {
        long fromId = BaseContext.getCurrentId();
        if (fromId == toId) {
           throw new CustomException("cant not add yourself");
        }
        int result = friendService.checkFriendshipStatus(fromId, toId);
        if (result == 1) {
           return R.success(1);
        } else if (result == 0) {
            return R.success(2);
        } else {
            return R.success(3);
        }
    }

    @PostMapping ("/friends/{id}")
    @ResponseBody
    public R<String> sendFriendRequest(@PathVariable("id") int toId,@RequestParam("msg")String msg ) {

        long fromId = BaseContext.getCurrentId();
        if (fromId == toId)
            return R.error("Do not add yourself");

        int result = friendService.sendFriendRequest(fromId, toId,msg);

        if (result == 2) {
            return R.success("Directly be friends");
        } else if (result == 1) {
            return R.success("Request have been sent");
        } else if (result == 0) {
            return R.error("Do not add again");
        } else {
            return R.error("You are already friends");
        }
    }

    @GetMapping("/friends/{id}")
    @ResponseBody
    public ResponseEntity<Object> acceptFriendRequest(@PathVariable("id") int toId, HttpSession session) {
        int fromId = (int) session.getAttribute("id");
        if (fromId == toId)
            return new ResponseEntity<>(ResultData.fail(201, "Do not add yourself"), HttpStatus.CREATED);

        int request = friendService.acceptFriendRequest(fromId, toId);
        System.out.println(request);
        if (request >= 1) {
            return new ResponseEntity<>(ResultData.success("Success to add friend"), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(ResultData.fail(201, "Fail to add friend"), HttpStatus.CREATED);
        }
    }

    @DeleteMapping("/friends/{id}")
    @ResponseBody
    public ResponseEntity<Object> rejectFriendRequest(@PathVariable("id") int toId, HttpSession session) {
        int fromId = (int) session.getAttribute("id");
        if (fromId == toId)
            return new ResponseEntity<>(ResultData.fail(201, "Can not reject yourself"), HttpStatus.CREATED);

        int request = friendService.rejectFriendRequest(fromId, toId);
        if (request >= 1) {
            return new ResponseEntity<>(ResultData.success("Success to reject request"), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(ResultData.fail(201, "Fail to reject request"), HttpStatus.CREATED);
        }
    }

    @GetMapping("/friends")
    @ResponseBody
    public ResponseEntity<Object> getFriendsList() {
        long id = BaseContext.getCurrentId();
        List<User> allFriends = friendService.getAllFriends(id);
        return new ResponseEntity<>(ResultData.success(allFriends), HttpStatus.OK);
    }

    @GetMapping("/friends/requests")
    @ResponseBody
    public ResponseEntity<Object> getRequestList(HttpSession session) {
        long id = (long) session.getAttribute("id");
        List<User> allRequests = friendService.getAllRequests(id);
        return new ResponseEntity<>(ResultData.success(allRequests), HttpStatus.OK);
    }

    @DeleteMapping("/friends/delete/{id}")
    @ResponseBody
    public ResponseEntity<Object> deleteFriendFromList(@PathVariable("id") int toId, HttpSession session) {
        long fromId = (long) session.getAttribute("id");
        if (fromId == toId)
            return new ResponseEntity<>(ResultData.fail(201, "Can not delete yourself"), HttpStatus.CREATED);

        int request = friendService.deleteFromFriendList(fromId, toId);
        if (request >= 1) {
            return new ResponseEntity<>(ResultData.success("Success to delete friend"), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(ResultData.fail(201, "Fail to delete friend"), HttpStatus.CREATED);
        }
    }

}
