package usyd.mingyi.animalcare.controller;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import usyd.mingyi.animalcare.common.R;
import usyd.mingyi.animalcare.config.ProjectProperties;
import usyd.mingyi.animalcare.dto.PostDto;
import usyd.mingyi.animalcare.pojo.Comment;
import usyd.mingyi.animalcare.pojo.Post;
import usyd.mingyi.animalcare.pojo.User;
import usyd.mingyi.animalcare.service.PostService;
import usyd.mingyi.animalcare.service.UserService;
import usyd.mingyi.animalcare.utils.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;

@RestController
@Slf4j
public class PostController {
    @Autowired
    UserService userService;
    @Autowired
    PostService postService;
    @Autowired
    RedisTemplate redisTemplate;
    @Autowired
    ProjectProperties projectProperties;
    @Autowired
    RestTemplate restTemplate;


    @PostMapping("/post")
    @ResponseBody
    @Transactional
    public R<String> upLoadPost(@RequestParam(value = "images",required = false) MultipartFile[] images,
                                                      @RequestParam("postTopic") String postTopic,
                                                      @RequestParam("postContent") String postContent,
                                                      @RequestParam("postTag") String postTag,
                                                      HttpServletRequest request) {


        String userName = JWTUtils.getUserName(request.getHeader("auth"));
        long id = BaseContext.getCurrentId();
        Post post = new Post();
        post.setUserId(id);
        post.setLove(0);
        post.setPostTime(System.currentTimeMillis());
        post.setPostContent(postContent);
        post.setTopic(postTopic);
        post.setTag(postTag);
        postService.addPost(post,userName,images);

        return R.success("Successfully upload");

    }



    //采用Restful风格进行一次传参
    @GetMapping("/post/{postId}")
    @ResponseBody
    public R<Post> getPost(@PathVariable int postId) {

        Post post = postService.queryPostById(postId);

        return R.success(post);
    }
    @DeleteMapping("/post/{postId}")
    public R<String> deletePost(@PathVariable("postId") long postId) {
        int i = postService.deletePost(postId, BaseContext.getCurrentId());
        return i>0?R.success("删除成功"):R.error("删除失败");
    }


    @GetMapping("/post")
    @ResponseBody
    public R<IPage<PostDto>> getPostsWithPagination(@RequestParam("currPage") int page, @RequestParam("pageSize") int pageSize) {

        IPage<PostDto> allPosts = postService.getAllPosts(page, pageSize);
        return R.success(allPosts);
    }


    @GetMapping("/love/{postId}")
    public R<String> love(@PathVariable("postId") long postId) {
        long id = BaseContext.getCurrentId();
        postService.love(id, postId);
        return R.success("success");
    }

    @DeleteMapping("/love/{postId}")
    public R<String>  cancelLove(@PathVariable("postId") long postId) {
        long id = BaseContext.getCurrentId();
        postService.cancelLove(id, postId);
        return R.success("success");
    }



    @GetMapping("/getPostByUserId/{id}")
    @ResponseBody
    public ResponseEntity<Object> getPostsByUserId(@PathVariable("id") int userId) {

        User user = userService.queryUserById(userId);
        if (user == null) {
            return new ResponseEntity<>(ResultData.fail(201, "No such user"), HttpStatus.CREATED);
        } else {
            List<Post> PostsByUserId = postService.getPostByUserId(userId);
            return new ResponseEntity<>(ResultData.success(PostsByUserId), HttpStatus.OK);
        }
    }

    @PostMapping("/comment/{postId}")
    @ResponseBody
    public ResponseEntity<Object> addComment(@PathVariable("postId") long postId, @RequestBody Map map) {


        String commentContent = (String) map.get("commentContent");
        Long id = BaseContext.getCurrentId();

        Comment comment = new Comment();
        comment.setCommentContent(commentContent);
        comment.setPostId(postId);
        comment.setCommentTime(System.currentTimeMillis());
        comment.setUserId(id);

        if (commentContent == null) {
            return new ResponseEntity<>(ResultData.fail(201, "Comment can not be null"), HttpStatus.CREATED);
        }
        if (postService.addComment(comment) != 1) {
            return new ResponseEntity<>(ResultData.fail(201, "Comment invalid"), HttpStatus.CREATED);
        }

        return new ResponseEntity<>(ResultData.success("Comment Added"), HttpStatus.OK);
    }
}
