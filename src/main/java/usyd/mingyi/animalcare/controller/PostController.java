package usyd.mingyi.animalcare.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import usyd.mingyi.animalcare.common.CustomException;
import usyd.mingyi.animalcare.common.R;
import usyd.mingyi.animalcare.dto.PostDto;
import usyd.mingyi.animalcare.pojo.Post;
import usyd.mingyi.animalcare.service.CommentService;
import usyd.mingyi.animalcare.service.PostService;
import usyd.mingyi.animalcare.service.UserService;
import usyd.mingyi.animalcare.utils.BaseContext;

import java.util.List;

@RestController
@Slf4j
public class PostController {
    @Autowired
    UserService userService;
    @Autowired
    PostService postService;
    @Autowired
    CommentService commentService;
    @Autowired
    RedisTemplate redisTemplate;
    @Autowired
    RestTemplate restTemplate;


    @PostMapping("/post")
    public R<String> upLoadPost(@RequestBody @Validated PostDto post) {

        post.setPostTime(System.currentTimeMillis());
        post.setUserId(BaseContext.getCurrentId());
        post.setCoverImage(post.getImages().get(0).getImageUrl());
        if (post.getEstimateDate() == null) {
            //立刻上传Post
            post.setPublishTime(System.currentTimeMillis());
            postService.addPost(post);
        } else {
            // 获取日期时间戳
            long targetTime = post.getEstimateDate().getTime();
            //放入MQ死信队列 设置TTL
            long currentTimeMillis = System.currentTimeMillis();
            long TTL = targetTime - currentTimeMillis;
            post.setPublishTime(targetTime);
            //放入消息队列
        }
        log.info("上传文件");
        return R.success("Successfully upload");
    }


    //采用Restful风格进行一次传参
    @GetMapping("/post/{postId}")
    public R<Post> getPost(@PathVariable Long postId) {
        Post post = postService.getPostById(postId, BaseContext.getCurrentId());
        if (!post.getVisible() && !post.getUserId().equals(BaseContext.getCurrentId())) {
            throw new CustomException("This post is currently invisible");
        }
        return R.success(post);
    }

    @DeleteMapping("/post/{postId}")
    public R<String> deletePost(@PathVariable("postId") long postId) {
        postService.deletePost(postId, BaseContext.getCurrentId());
        return R.success("Successfully delete post");
    }


    @GetMapping("/post")
    public R<IPage<PostDto>> getPostsWithPagination(@RequestParam("currPage") Long page, @RequestParam("pageSize") Integer pageSize, @RequestParam("order") Integer order) {
        IPage<PostDto> allPosts = postService.getAllPosts(page, pageSize, order);
        return R.success(allPosts);
    }


    @GetMapping("/love/{postId}")
    public R<String> love(@PathVariable("postId") long postId) {
        long id = BaseContext.getCurrentId();
        postService.love(id, postId);
        return R.success("success");
    }

    @DeleteMapping("/love/{postId}")
    public R<String> cancelLove(@PathVariable("postId") long postId) {
        long id = BaseContext.getCurrentId();
        postService.cancelLove(id, postId);
        return R.success("success");
    }


    @GetMapping("/posts")
    public R<List<PostDto>> getMyPosts() {
        long userId = BaseContext.getCurrentId();
        List<PostDto> myPosts = postService.getPostByUserId(userId);
        return R.success(myPosts);
    }

    @PutMapping("/post/{postId}")
    public R<String> changeVisibility(@PathVariable("postId") long postId, @RequestParam("visibility") Boolean visibility) {
        System.out.println(visibility);
        Post post = postService.getById(postId);
        if (!post.getUserId().equals(BaseContext.getCurrentId())) {
            return R.error("No right to access");
        } else {
            Post newPost = new Post();
            newPost.setPostId(postId);
            newPost.setVisible(visibility);
            if (postService.updateById(newPost)) {
                return R.success("Update success");
            } else {
                return R.error("No change happen");
            }
        }
    }


    @GetMapping("/search/{keywords}")
    public R<List<Post>> getPosts(@PathVariable("keywords") String keywords) {
        return null;
    }


    @GetMapping("/search/trendingPosts")
    public ResponseEntity<Object> getTrendingPosts() {

      /*  //List<Post> posts = RedisUtils.getHots(redisTemplate);

        return new ResponseEntity<>(ResultData.success(posts), HttpStatus.OK);*/
        return null;
    }


    @GetMapping("/mention/posts")
    public R<Page<Post>> getAllMentionedPosts(@RequestParam("current") Long current,
                                              @RequestParam("pageSize") Integer pageSize) {
        return R.success(postService.getAllPostMentionedToMe(BaseContext.getCurrentId(), current, pageSize));

    }


}
