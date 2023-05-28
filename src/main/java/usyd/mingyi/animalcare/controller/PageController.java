package usyd.mingyi.animalcare.controller;


import io.netty.util.internal.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import usyd.mingyi.animalcare.config.ProjectProperties;
import usyd.mingyi.animalcare.pojo.Comment;
import usyd.mingyi.animalcare.pojo.Pet;
import usyd.mingyi.animalcare.pojo.Post;
import usyd.mingyi.animalcare.pojo.User;
import usyd.mingyi.animalcare.service.FriendService;
import usyd.mingyi.animalcare.service.PetService;
import usyd.mingyi.animalcare.service.PostService;
import usyd.mingyi.animalcare.service.UserService;
import usyd.mingyi.animalcare.utils.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@RestController
public class PageController {

    @Autowired
    UserService userService;
    @Autowired
    PostService postService;
    @Autowired
    PetService petService;
    @Autowired
    FriendService friendService;
    @Autowired
    RedisTemplate redisTemplate;
    @Autowired
    ProjectProperties projectProperties;
    @Autowired
    RestTemplate restTemplate;





    @GetMapping("/getCommentsByPostId/{postId}")
    @ResponseBody
    public ResponseEntity<Object> getCommentsByPostId(@PathVariable("postId") int postId, HttpServletRequest request) {

        if (postService.queryPostById(postId) == null) {
            return new ResponseEntity<>(ResultData.fail(201, "No such post"), HttpStatus.CREATED);
        } else {
            List<Comment> CommentsByPostId = postService.getCommentsByPostId(postId);
            return new ResponseEntity<>(ResultData.success(CommentsByPostId), HttpStatus.OK);
        }


    }




    @GetMapping("/profile/{userId}")
    public ResponseEntity<Object> getProfile(@PathVariable("userId") int userId, HttpSession session) {

        User profile = userService.getProfile(userId);

        return new ResponseEntity<>(ResultData.success(profile), HttpStatus.OK);
    }

    @GetMapping("/profile")
    public ResponseEntity<Object> getMyProfile() {
        Long id = BaseContext.getCurrentId();
        User profile = userService.getProfile(id);
        return new ResponseEntity<>(ResultData.success(profile), HttpStatus.OK);
    }


    @GetMapping("/search/{keywords}")
    @ResponseBody
    public ResponseEntity<Object> getPosts(@PathVariable("keywords") String keywords) {
        keywords = "*"+ keywords + "*";
        List<Post> postsByKeywords = postService.getPostsByKeywords(keywords);
        return new ResponseEntity<>(ResultData.success(postsByKeywords), HttpStatus.OK);
    }

    @GetMapping("/friends/status/{id}")
    @ResponseBody
    public ResponseEntity<Object> getFriendshipStatus(@PathVariable("id") int toId, HttpSession session) {
        int fromId = (int) session.getAttribute("id");
        if (fromId == toId) {
            return new ResponseEntity<>(ResultData.fail(201, "Do not add yourself"), HttpStatus.CREATED);
        }
        int result = friendService.checkFriendshipStatus(fromId, toId);
        if (result == 1) {
            return new ResponseEntity<>(ResultData.success("Friend"), HttpStatus.OK);
        } else if (result == 0) {
            return new ResponseEntity<>(ResultData.success("Requesting"), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(ResultData.success("Nothing"), HttpStatus.OK);
        }
    }

    @GetMapping("/friends/{id}")
    @ResponseBody
    public ResponseEntity<Object> sendFriendRequest(@PathVariable("id") int toId, HttpSession session) {
        int fromId = (int) session.getAttribute("id");
        if (fromId == toId)
            return new ResponseEntity<>(ResultData.fail(201, "Do not add yourself"), HttpStatus.CREATED);

        int result = friendService.sendFriendRequest(fromId, toId);

        if (result == 2) {
            return new ResponseEntity<>(ResultData.success("Directly be friends"), HttpStatus.OK);

        } else if (result == 1) {
            return new ResponseEntity<>(ResultData.success("Request have been sent"), HttpStatus.OK);

        } else if (result == 0) {
            return new ResponseEntity<>(ResultData.fail(201, "Do not add again"), HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>(ResultData.fail(201, "You are already friends"), HttpStatus.CREATED);
        }
    }

    @PostMapping("/friends/{id}")
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
    public ResponseEntity<Object> getFriendsList(HttpSession session) {
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

    @GetMapping("/search/trendingPosts")
    @ResponseBody
    public ResponseEntity<Object> getTrendingPosts() {

      /*  //List<Post> posts = RedisUtils.getHots(redisTemplate);

        return new ResponseEntity<>(ResultData.success(posts), HttpStatus.OK);*/
        return  null;
    }


}
