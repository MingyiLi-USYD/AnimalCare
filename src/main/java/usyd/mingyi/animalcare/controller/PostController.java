package usyd.mingyi.animalcare.controller;

import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
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
import usyd.mingyi.animalcare.config.ProjectProperties;
import usyd.mingyi.animalcare.pojo.Comment;
import usyd.mingyi.animalcare.pojo.Post;
import usyd.mingyi.animalcare.pojo.User;
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


    @PostMapping("/post/newPost")
    @ResponseBody
    public ResponseEntity<Object> upLoadPost(@RequestBody Map map,HttpServletRequest request ) {

        String auth = request.getHeader("auth");
        String userName = JWTUtils.getUserName(auth);

        String fileDiskLocation = projectProperties.fileDiskLocation;
        ;
        String projectPrefix = projectProperties.projectPrefix;
        String data = "";//实体部分数
        String suffix = "";//图片后缀，用以识别哪种格式数据

        ArrayList<String> list = (ArrayList<String>) map.get("base64Data");
        String postTopic = (String) map.get("postTopic");
        String postContent = (String) map.get("postContent");
        String postTag = (String) map.get("postTag");

        //String userName = (String) session.getAttribute("userName");
        long id = BaseContext.getCurrentId();

        Post post = new Post();
        post.setUserId(id);
        post.setLove(0);
        post.setPostTime(System.currentTimeMillis());
        post.setPostContent(postContent);
        post.setTopic(postTopic);
        post.setTag(postTag);
        if (postService.addPost(post) != 1) {
            return new ResponseEntity<>(ResultData.fail(201, "Content invalid"), HttpStatus.CREATED);
        }
        Long postId = post.getPostId();

        try {
            for (String base64Data : list) {
                if (ImageUtil.checkImage(base64Data)) {
                    suffix = ImageUtil.getSuffix(base64Data);
                    data = ImageUtil.getData(base64Data);
                } else {
                    return new ResponseEntity<>(ResultData.fail(201, "File invalid"), HttpStatus.CREATED);
                }


                String tempFileName = UUID.randomUUID().toString() + suffix; //文件名


                String path = fileDiskLocation + userName; //文件路径

                try {
                    ImageUtil.convertBase64ToFile(data, path, tempFileName);
                    postService.addImage(postId, projectPrefix + userName + "/" + tempFileName);

                } catch (Exception e) {
                    e.printStackTrace();
                    return new ResponseEntity<>(ResultData.fail(201, "File invalid"), HttpStatus.CREATED);

                }
            }
        }catch (NullPointerException e) {
            e.printStackTrace();
        }

        return new ResponseEntity<>(ResultData.success("Success upload files"), HttpStatus.OK);

    }
/*
    @PostMapping("/post/android/newPost")
    @ResponseBody
    @Transactional
    public ResponseEntity<Object> upLoadPostInAndroid(@RequestParam(value = "images[]",required = false) MultipartFile[] images,
                                                      @RequestParam("postTopic") String postTopic,
                                                      @RequestParam("postContent") String postContent,
                                                      @RequestParam("postTag") String postTag,
                                                      HttpServletRequest request) {
        String fileDiskLocation = projectProperties.fileDiskLocation;
        ;
        String projectPrefix = projectProperties.projectPrefix;

        HttpSession session = request.getSession();
        System.out.println(images);
        log.info(postTopic);
        String userName = (String) session.getAttribute("userName");
        int id = (int) session.getAttribute("id");
        Post post = new Post();
        post.setUserId(id);
        post.setLove(0);
        post.setPosTime(System.currentTimeMillis());
        post.setPostContent(postContent);
        post.setTopic(postTopic);
        post.setTag(postTag);

        if (postService.addPost(post) != 1) {
            return new ResponseEntity<>(ResultData.fail(201, "Content invalid"), HttpStatus.CREATED);
        }
        Long postId = post.getPostId();

        try {
            for(int i=0;i<images.length;i++){

                String originalName = images[i].getOriginalFilename();
                System.out.println(originalName);
                String suffix = originalName.substring(originalName.lastIndexOf("."));
                String tempFileName = UUID.randomUUID().toString() + suffix; //文件名
                String path = fileDiskLocation + userName; //文件路径
                postService.addImage(postId, projectPrefix + userName + "/" + tempFileName);

                File newFile = new File(path+ File.separator + tempFileName);
                if(!newFile.getParentFile().exists()){
                    newFile.getParentFile().mkdirs();
                }
                images[i].transferTo(newFile);
            }

        }catch (NullPointerException | IOException e) {
            e.printStackTrace();
        }

        return new ResponseEntity<>(ResultData.success("Success upload files"), HttpStatus.OK);

    }*/

    @GetMapping("/getPosts")
    @ResponseBody
    public ResponseEntity<Object> getPosts(@RequestParam("currPage") int page, @RequestParam("pageSize") int pageSize) {

        List<Post> allPosts = postService.getAllPosts(page, pageSize);
        return new ResponseEntity<>(ResultData.success(allPosts), HttpStatus.OK);
    }

    @GetMapping("/getPostsOrderByLove")
    @ResponseBody
    public ResponseEntity<Object> getPostsOrderByLove(@RequestParam("currPage") int currPage, @RequestParam("pageSize") int pageSize) {
        List<Post> allPosts = postService.getAllPostsOrderByLove(currPage, pageSize);
        return new ResponseEntity<>(ResultData.success(allPosts), HttpStatus.OK);
    }


    //采用Restful风格进行一次传参
    @GetMapping("/getPost/{postId}")
    @ResponseBody
    public ResponseEntity<Object> getPost(@PathVariable int postId, HttpServletRequest request) {

        HttpSession session = request.getSession();
        int id = (int) session.getAttribute("id");
        boolean loved = postService.checkLoved(id, postId);
        Post postCache = RedisUtils.getPost(redisTemplate, postId, loved);
        if (postCache != null) {
            return new ResponseEntity<>(ResultData.success(postCache), HttpStatus.OK);
        }


        Post post = postService.queryPostById(postId);

        if (post != null) {
            post = postService.queryPostById(postId);
            post.setLoved(loved);
            RedisUtils.putPost(redisTemplate, post);
            return new ResponseEntity<>(ResultData.success(post), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(ResultData.fail(201, "No such post found"), HttpStatus.CREATED);
        }
//        }
    }


    @GetMapping("/love/{postId}")
    public ResponseEntity<Object> love(@PathVariable("postId") int postId, HttpSession session) {
        int id = (int) session.getAttribute("id");
        String key = "post" + postId;
        if (redisTemplate.hasKey(key))
            redisTemplate.opsForHash().increment(key, "love", 1);
        postService.love(id, postId);
        return new ResponseEntity<>(ResultData.success("OK"), HttpStatus.OK);
    }

    @DeleteMapping("/love/{postId}")
    public ResponseEntity<Object> cancelLove(@PathVariable("postId") int postId, HttpSession session) {
        int id = (int) session.getAttribute("id");
        String key = "post" + postId;
        if (redisTemplate.hasKey(key))
            redisTemplate.opsForHash().increment(key, "love", -1);
        postService.cancelLove(id, postId);
        return new ResponseEntity<>(ResultData.success("OK"), HttpStatus.OK);
    }

    @DeleteMapping("/post/deletePost/{postId}")
    public ResponseEntity<Object> deletePost(@PathVariable("postId") int postId, HttpSession session) {

        int id = (int) session.getAttribute("id");
        if (postService.deletePost(postId, id) == 0) {
            return new ResponseEntity<>(ResultData.fail(201, "Fail to delete post, No such post found"), HttpStatus.CREATED);
        } else {
            String key = "post" + postId;
            if (redisTemplate.hasKey(key))
                redisTemplate.delete(key);
            return new ResponseEntity<>(ResultData.success("OK"), HttpStatus.OK);
        }

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

    @PostMapping("/Post/addComment/{postId}")
    @ResponseBody
    public ResponseEntity<Object> addComment(@PathVariable("postId") long postId, @RequestBody Map map, HttpServletRequest request) {

        HttpSession session = request.getSession();
        String commentContent = (String) map.get("commentContent");
        Long id = BaseContext.getCurrentId();
        String userAvatar = (String) session.getAttribute("userAvatar");
        String userName = (String) session.getAttribute("userName");

        Comment comment = new Comment();
        comment.setCommentContent(commentContent);
        comment.setPostId(postId);
        comment.setCommentTime(System.currentTimeMillis());
        comment.setUserId(id);
        comment.setUserAvatar(userAvatar);
        comment.setUserName(userName);

        if (commentContent == null) {
            return new ResponseEntity<>(ResultData.fail(201, "Comment can not be null"), HttpStatus.CREATED);
        }
        if (postService.addComment(comment) != 1) {
            return new ResponseEntity<>(ResultData.fail(201, "Comment invalid"), HttpStatus.CREATED);
        }

        return new ResponseEntity<>(ResultData.success("Comment Added"), HttpStatus.OK);
    }
}
