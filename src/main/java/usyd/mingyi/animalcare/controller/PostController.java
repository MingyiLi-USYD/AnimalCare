package usyd.mingyi.animalcare.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import usyd.mingyi.animalcare.common.R;
import usyd.mingyi.animalcare.dto.PostDto;
import usyd.mingyi.animalcare.pojo.Post;
import usyd.mingyi.animalcare.service.CommentService;
import usyd.mingyi.animalcare.service.PostService;
import usyd.mingyi.animalcare.service.UserService;
import usyd.mingyi.animalcare.utils.BaseContext;
import usyd.mingyi.animalcare.utils.ResultData;

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
    @ResponseBody
    @Transactional
    public R<String> upLoadPost(@RequestBody Post post) {
        post.setUserId(BaseContext.getCurrentId());
        postService.addPost(post);
        log.info("上传文件");
        return R.success("Successfully upload");

    }



    //采用Restful风格进行一次传参
    @GetMapping("/post/{postId}")
    @ResponseBody
    public R<Post> getPost(@PathVariable long postId) {

        Post post = postService.queryPostById(postId,BaseContext.getCurrentId());

        return R.success(post);
    }
    @DeleteMapping("/post/{postId}")
    public R<String> deletePost(@PathVariable("postId") long postId) {
        int i = postService.deletePost(postId, BaseContext.getCurrentId());
        return i>0?R.success("删除成功"):R.error("删除失败");
    }


    @GetMapping("/post")
    @ResponseBody
    public R<IPage<PostDto>> getPostsWithPagination(@RequestParam("currPage") int page, @RequestParam("pageSize") int pageSize,@RequestParam("order") int order) {

        IPage<PostDto> allPosts = postService.getAllPosts(page, pageSize,order);
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


    @GetMapping("/posts")
    @ResponseBody
    public R<List<Post>> getMyPosts() {
        long userId = BaseContext.getCurrentId();
        List<Post> myPosts = postService.getPostByUserId(userId);
        return R.success(myPosts);
    }

    @PutMapping("/post/{postId}")
    public R<String> changeVisibility(@PathVariable("postId") long postId,@RequestParam("visibility")Boolean visibility){
        System.out.println(visibility);
        Post post = postService.getById(postId);
        if(!post.getUserId().equals(BaseContext.getCurrentId())){
            return R.error("No right to access");
        }else {
            Post newPost = new Post();
            newPost.setPostId(postId);
            newPost.setVisible(visibility);
            if( postService.updateById(newPost)){
                return R.success("Update success");
            }else {
                return  R.error("No change happen");
            }
        }
    }



    @GetMapping("/search/{keywords}")
    @ResponseBody
    public ResponseEntity<Object> getPosts(@PathVariable("keywords") String keywords) {
        keywords = "*"+ keywords + "*";
        List<Post> postsByKeywords = postService.getPostsByKeywords(keywords);
        return new ResponseEntity<>(ResultData.success(postsByKeywords), HttpStatus.OK);
    }


    @GetMapping("/search/trendingPosts")
    @ResponseBody
    public ResponseEntity<Object> getTrendingPosts() {

      /*  //List<Post> posts = RedisUtils.getHots(redisTemplate);

        return new ResponseEntity<>(ResultData.success(posts), HttpStatus.OK);*/
        return  null;
    }


}
