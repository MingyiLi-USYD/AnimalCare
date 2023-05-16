package usyd.mingyi.animalcare.controller;


import io.netty.util.internal.StringUtil;
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


    //Two main ways to receive data from frontend map and pojo, we plan to use pojo to receive data for better maintain in future
    @PostMapping("/login")
    @ResponseBody
    public ResponseEntity<Object> login(@RequestBody User userInfo, HttpSession session) {

        String username = userInfo.getUserName();
        String password = userInfo.getPassword();
        String encryptedPassword = userService.queryPassword(username);

        if (encryptedPassword == null) {
            return new ResponseEntity<>(ResultData.fail(401, "No such user"), HttpStatus.UNAUTHORIZED);
        } else {
            String decode = JasyptEncryptorUtils.decode(encryptedPassword);
            if (!decode.equals(password)) {
                return new ResponseEntity<>(ResultData.fail(401, "Password error"), HttpStatus.UNAUTHORIZED);
            }
        }

        User user = userService.queryUser(username, encryptedPassword);

        if (user != null) {

            session.setAttribute("id", user.getId());
            session.setAttribute("userName", user.getUserName());
            session.setAttribute("nickName", user.getNickName());
            session.setAttribute("userAvatar", user.getUserImageAddress());
            return new ResponseEntity<>(ResultData.success(user.getId()), HttpStatus.OK);

        } else {
            return new ResponseEntity<>(ResultData.fail(401, "Password error"), HttpStatus.UNAUTHORIZED);

        }
    }

    @GetMapping("/logout")
    @ResponseBody
    public ResponseEntity<Object> logout(HttpSession session, SessionStatus sessionStatus) {
        session.invalidate();
        sessionStatus.setComplete();
        return new ResponseEntity<>(ResultData.success("Success to logout"), HttpStatus.OK);

    }

    @PostMapping("/signup")
    @ResponseBody
    public ResponseEntity<Object> signup(@RequestBody User userInfo) {
        if(StringUtil.isNullOrEmpty(userInfo.getUserImageAddress())){
            userInfo.setUserImageAddress("http://35.189.24.208:8080/api/images/default.jpg");
        }
        userInfo.setPassword(JasyptEncryptorUtils.encode(userInfo.getPassword()));
        userInfo.setUuid(UUID.randomUUID().toString());
        String randomNickname = RandomUtils.getRandomNickname(restTemplate);
        userInfo.setNickName(randomNickname);
        int i = userService.addUser(userInfo);
        if (i >= 1) {
            return new ResponseEntity<>(ResultData.success("Signup success"), HttpStatus.OK);

        } else {
            return new ResponseEntity<>(ResultData.fail(201, "Signup fail"), HttpStatus.CREATED);
        }

    }

    @GetMapping("/username")
    @ResponseBody
    public ResponseEntity<Object> usernameCheck(@RequestParam("userName") String userName) {

        User user = userService.queryUserByUsername(userName);
        if (user == null) {
            return new ResponseEntity<>(ResultData.success(null), HttpStatus.OK);
        }
        return new ResponseEntity<>(ResultData.fail(201, "Fail"), HttpStatus.CREATED);

    }

    @PostMapping("/email")
    @ResponseBody
    public ResponseEntity<Object> sendEmailByUsername(@RequestBody Map map) {
        String email = (String) map.get("email");
        String userName = (String) map.get("userName");
        userService.sendEmail(email, userName);
        return new ResponseEntity<>(ResultData.success(null), HttpStatus.OK);
    }

    @PostMapping("/validate")
    @ResponseBody
    public ResponseEntity<Object> validateCode(@RequestBody Map map) {
        String code = (String) map.get("code");
        String userName = (String) map.get("userName");
        String password = (String) map.get("password");

        if (redisTemplate.hasKey(userName)) {
            if (redisTemplate.opsForValue().get(userName).toString().equals(code)) {
                if (userService.updatePassword(userName, JasyptEncryptorUtils.encode(password)) >= 1) {
                    redisTemplate.delete(userName);
                    return new ResponseEntity<>(ResultData.success("Success to change password "), HttpStatus.OK);
                } else {
                    return new ResponseEntity<>(ResultData.fail(201, "Fail to change password"), HttpStatus.CREATED);
                }

            } else {
                return new ResponseEntity<>(ResultData.fail(201, "Code not equal"), HttpStatus.CREATED);
            }
        }
        return new ResponseEntity<>(ResultData.fail(201, "No code in the system"), HttpStatus.CREATED);
    }

    @PostMapping("/edit")
    @ResponseBody
    public ResponseEntity<Object> updateUserInfo(@RequestBody User userInfo, HttpSession session) {
        String fileDiskLocation = projectProperties.fileDiskLocation;
        ;
        String projectPrefix = projectProperties.projectPrefix;
        int id = (int) session.getAttribute("id");
        String userName = (String) session.getAttribute("userName");
        userInfo.setId(id);
        String avatarUrl = userInfo.getUserImageAddress();
        if (!StringUtil.isNullOrEmpty(avatarUrl) && ImageUtil.checkImage(avatarUrl)) {
            //更改照片
            String suffix = ImageUtil.getSuffix(avatarUrl);
            String data = ImageUtil.getData(avatarUrl);
            String tempFileName = UUID.randomUUID().toString() + suffix; //文件名
            String path = fileDiskLocation + userName; //文件路径
            try {
                ImageUtil.convertBase64ToFile(data, path, tempFileName);
                userInfo.setUserImageAddress(projectPrefix + userName + "/" + tempFileName);
                userService.updateUser(userInfo);
                //删掉本地之前的头像（未写）
            } catch (Exception e) {
                e.printStackTrace();
                return new ResponseEntity<>(ResultData.fail(201, "File invalid"), HttpStatus.CREATED);
            }
        } else {
            userService.updateUser(userInfo);
            //删掉本地之前的头像（未写）
        }
        return new ResponseEntity<>(ResultData.success("Update success"), HttpStatus.OK);
    }

    @PostMapping("/android/edit")
    @ResponseBody
    public ResponseEntity<Object> updateUserInfoInAndroid(@RequestParam(value = "avatar",required = false) MultipartFile avatar,
                                                          @RequestParam("nickName") String nickName,
                                                          @RequestParam("description") String description,
                                                          HttpSession session) {
        String fileDiskLocation = projectProperties.fileDiskLocation;
        ;
        String projectPrefix = projectProperties.projectPrefix;
        int id = (int) session.getAttribute("id");
        String userName = (String) session.getAttribute("userName");
        User userInfo = new User();
        userInfo.setNickName(nickName);
        userInfo.setDescription(description);
        userInfo.setId(id);

        try {
            //存入用户头像
            String originalName = avatar.getOriginalFilename();
            System.out.println(originalName);
            String suffix = originalName.substring(originalName.lastIndexOf("."));
            String tempFileName = UUID.randomUUID().toString() + suffix; //文件名
            String path = fileDiskLocation + userName; //文件路径
            File newFile = new File(path+ File.separator + tempFileName);
            if(!newFile.getParentFile().exists()){
                newFile.getParentFile().mkdirs();
            }
            avatar.transferTo(newFile);
            userInfo.setUserImageAddress(projectPrefix + userName + "/" + tempFileName);
        }catch (NullPointerException | IOException e) {
            e.printStackTrace();
        }
        userService.updateUser(userInfo);
        return new ResponseEntity<>(ResultData.success("Update success"), HttpStatus.OK);
    }


    @PostMapping("/post/newPost")
    @ResponseBody
    public ResponseEntity<Object> upLoadPost(@RequestBody Map map, HttpServletRequest request) {
        String fileDiskLocation = projectProperties.fileDiskLocation;
        ;
        String projectPrefix = projectProperties.projectPrefix;
        String data = "";//实体部分数
        String suffix = "";//图片后缀，用以识别哪种格式数据

        ArrayList<String> list = (ArrayList<String>) map.get("base64Data");
        String postTopic = (String) map.get("postTopic");
        String postContent = (String) map.get("postContent");
        String postTag = (String) map.get("postTag");
        HttpSession session = request.getSession();

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
        Integer postId = post.getPostId();

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

    @PostMapping("/post/android/newPost")
    @ResponseBody
    @Transactional
    public ResponseEntity<Object> upLoadPostInAndroid(@RequestParam(value = "images",required = false) MultipartFile[] images,
                                                                                @RequestParam("postTopic") String postTopic,
                                                                                @RequestParam("postContent") String postContent,
                                                                                @RequestParam("postTag") String postTag,
                                                                                HttpServletRequest request) {
        String fileDiskLocation = projectProperties.fileDiskLocation;
        ;
        String projectPrefix = projectProperties.projectPrefix;

        HttpSession session = request.getSession();

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
        Integer postId = post.getPostId();

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

    }

    @GetMapping("/getPosts")
    @ResponseBody
    public ResponseEntity<Object> getPosts(@RequestParam("currPage") int currPage, @RequestParam("pageSize") int pageSize) {

        List<Post> allPosts = postService.getAllPosts(currPage, pageSize);
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
    public ResponseEntity<Object> addComment(@PathVariable("postId") int postId, @RequestBody Map map, HttpServletRequest request) {

        HttpSession session = request.getSession();
        String commentContent = (String) map.get("commentContent");
        int id = (int) session.getAttribute("id");
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


    @PostMapping("/pet/newPet")
    public ResponseEntity<Object> addPet(@RequestBody Map map, HttpSession session) {
        String fileDiskLocation = projectProperties.fileDiskLocation;
        ;
        String projectPrefix = projectProperties.projectPrefix;
        int id = (int) session.getAttribute("id");
        String userName = (String) session.getAttribute("userName");
        String data = "";//实体部分数
        String suffix = "";//图片后缀，用以识别哪种格式数据
        String avatarUrl = (String) map.get("avatarUrl");
        String name = (String) map.get("petName");
        String category = (String) map.get("category");
        List<String> list = (List<String>) map.get("petImageListArr");
        String petDescription = (String) map.get("petDescription");
        Pet pet = new Pet();
        pet.setCategory(category);
        pet.setPetName(name);
        pet.setUserId(id);
        pet.setPetDescription(petDescription);

        if (StringUtil.isNullOrEmpty(avatarUrl)) {
            if (category.equals("dog")) {
                pet.setPetImageAddress(projectPrefix + "dogDefault.jpg");
            } else {
                pet.setPetImageAddress(projectPrefix + "catDefault.jpg");
            }


        } else if (ImageUtil.checkImage(avatarUrl)) {
            suffix = ImageUtil.getSuffix(avatarUrl);
            data = ImageUtil.getData(avatarUrl);
            String tempFileName = UUID.randomUUID().toString() + suffix; //文件名
            String path = fileDiskLocation + userName; //文件路径
            try {
                ImageUtil.convertBase64ToFile(data, path, tempFileName);
                pet.setPetImageAddress(projectPrefix + userName + "/" + tempFileName);


            } catch (Exception e) {
                e.printStackTrace();
                return new ResponseEntity<>(ResultData.fail(201, "File invalid"), HttpStatus.CREATED);
            }
        } else {
            return new ResponseEntity<>(ResultData.fail(201, "File invalid"), HttpStatus.CREATED);
        }
        petService.addPet(pet);
        Integer petId = pet.getPetId();
        if (list == null) return new ResponseEntity<>(ResultData.success("OK"), HttpStatus.OK);

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
                petService.addImage(petId, projectPrefix + userName + "/" + tempFileName);
            } catch (Exception e) {
                e.printStackTrace();
                return new ResponseEntity<>(ResultData.fail(201, "File invalid"), HttpStatus.CREATED);

            }
        }

        return new ResponseEntity<>(ResultData.success("OK"), HttpStatus.OK);
    }

    @PostMapping("/pet/android/newPet")
    public ResponseEntity<Object> addPetInAndroid(@RequestParam(value = "images",required = false) MultipartFile[] images,
                                                  @RequestParam(value = "avatar",required = false) MultipartFile avatar,
                                                  @RequestParam("petName") String petName,
                                                  @RequestParam("category") String category,
                                                  @RequestParam("petDescription") String petDescription,
                                                  HttpServletRequest request, HttpSession session) {
        String fileDiskLocation = projectProperties.fileDiskLocation;
        ;
        String projectPrefix = projectProperties.projectPrefix;
        int id = (int) session.getAttribute("id");
        String userName = (String) session.getAttribute("userName");
        Pet pet = new Pet();
        pet.setUserId(id);
        pet.setCategory(category);
        pet.setPetName(petName);
        pet.setPetDescription(petDescription);

        try {
         //存入宠物头像
                String originalName = avatar.getOriginalFilename();
                System.out.println(originalName);
                String suffix = originalName.substring(originalName.lastIndexOf("."));
                String tempFileName = UUID.randomUUID().toString() + suffix; //文件名
                String path = fileDiskLocation + userName; //文件路径
                File newFile = new File(path+ File.separator + tempFileName);
                if(!newFile.getParentFile().exists()){
                    newFile.getParentFile().mkdirs();
                }
                avatar.transferTo(newFile);
            pet.setPetImageAddress(projectPrefix + userName + "/" + tempFileName);
        }catch (NullPointerException | IOException e) {
            e.printStackTrace();
        }


        petService.addPet(pet);
        Integer petId = pet.getPetId();
        try {
            for(int i=0;i<images.length;i++){

                String originalName = images[i].getOriginalFilename();
                System.out.println(originalName);
                String suffix = originalName.substring(originalName.lastIndexOf("."));
                String tempFileName = UUID.randomUUID().toString() + suffix; //文件名
                String path = fileDiskLocation + userName; //文件路径
                petService.addImage(petId, projectPrefix + userName + "/" + tempFileName);
                File newFile = new File(path+ File.separator + tempFileName);
                if(!newFile.getParentFile().exists()){
                    newFile.getParentFile().mkdirs();
                }
                images[i].transferTo(newFile);
            }

        }catch (NullPointerException | IOException e) {
            e.printStackTrace();
        }

        return new ResponseEntity<>(ResultData.success("OK"), HttpStatus.OK);
    }

    @GetMapping("/getPetList")
    public ResponseEntity<Object> getPetList(HttpSession session) {
        int id = (int) session.getAttribute("id");
        List<Pet> petList = petService.getPetList(id);
        return new ResponseEntity<>(ResultData.success(petList), HttpStatus.OK);
    }

    @GetMapping("/pet/{petId}")
    public ResponseEntity<Object> getPet(@PathVariable("petId") int petId, HttpSession session) {
        int id = (int) session.getAttribute("id");
        Pet pet = petService.getPet(petId, id);

        if (pet != null) {
            return new ResponseEntity<>(ResultData.success(pet), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(ResultData.fail(201, "No such pet found"), HttpStatus.CREATED);
        }
    }

    @DeleteMapping("/pet/{petId}")
    public ResponseEntity<Object> deletePet(@PathVariable("petId") int petId, HttpSession session) {
        int id = (int) session.getAttribute("id");
        int i = petService.deletePet(petId, id);
        if (i == 0) return new ResponseEntity<>(ResultData.fail(201, "Fail to delete for no such pet"), HttpStatus.CREATED);
        return new ResponseEntity<>(ResultData.success("OK"), HttpStatus.OK);
    }

    @PutMapping("/pet/{petId}")
    public ResponseEntity<Object> updatePet(@PathVariable("petId") int petId, HttpSession session) {

        return null;
    }


    @GetMapping("/profile/{userId}")
    public ResponseEntity<Object> getProfile(@PathVariable("userId") int userId, HttpSession session) {

        User profile = userService.getProfile(userId);

        return new ResponseEntity<>(ResultData.success(profile), HttpStatus.OK);
    }

    @GetMapping("/profile")
    public ResponseEntity<Object> getMyProfile(HttpSession session) {
        int id = (int) session.getAttribute("id");
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
        int id = (int) session.getAttribute("id");
        List<User> allFriends = friendService.getAllFriends(id);
        return new ResponseEntity<>(ResultData.success(allFriends), HttpStatus.OK);
    }

    @GetMapping("/friends/requests")
    @ResponseBody
    public ResponseEntity<Object> getRequestList(HttpSession session) {
        int id = (int) session.getAttribute("id");
        List<User> allRequests = friendService.getAllRequests(id);
        return new ResponseEntity<>(ResultData.success(allRequests), HttpStatus.OK);
    }

    @DeleteMapping("/friends/delete/{id}")
    @ResponseBody
    public ResponseEntity<Object> deleteFriendFromList(@PathVariable("id") int toId, HttpSession session) {
        int fromId = (int) session.getAttribute("id");
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

        List<Post> posts = RedisUtils.getHots(redisTemplate);

        return new ResponseEntity<>(ResultData.success(posts), HttpStatus.OK);
    }


}
