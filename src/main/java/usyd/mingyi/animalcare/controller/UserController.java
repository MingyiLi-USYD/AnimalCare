package usyd.mingyi.animalcare.controller;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.corundumstudio.socketio.SocketIOClient;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import io.netty.util.internal.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import usyd.mingyi.animalcare.common.CustomException;
import usyd.mingyi.animalcare.common.R;
import usyd.mingyi.animalcare.component.ClientCache;
import usyd.mingyi.animalcare.config.rabbitMQ.MQConfig;
import usyd.mingyi.animalcare.dto.UserDto;
import usyd.mingyi.animalcare.pojo.User;
import usyd.mingyi.animalcare.service.UserService;
import usyd.mingyi.animalcare.utils.*;

import java.util.*;
import java.util.stream.Collectors;


@RestController
@Slf4j
public class UserController {
    @Autowired
    UserService userService;
    @Autowired
    RedisTemplate redisTemplate;

    @Autowired
    RabbitTemplate rabbitTemplate;
    @Autowired
    RestTemplate restTemplate;
    @Autowired
    ClientCache clientCache;
    @Autowired
    ObjectMapper objectMapper;

    //Two main ways to receive data from frontend map and pojo, we plan to use pojo to receive data for better maintain in future
    @PostMapping("/login")
    @ResponseBody
    public R<Map> login(@RequestBody User userInfo) {

        log.info("登录");
        String username = userInfo.getUsername();
        String password = userInfo.getPassword();
        User user = userService.getUserByUsername(username);
        if (user == null) {
            throw new CustomException("No such user");
        } else {
            if (!PasswordUtils.verifyPassword(password,user.getPassword())) {
                throw new CustomException("Password Error");
            }
        }

            Map<String, String> map = new HashMap<>();
            map.put("serverToken",JWTUtils.generateToken(user));
            map.put("firebaseToken",JWTUtils.generateFirebaseToken(String.valueOf(user.getUserId())));
            return R.success(map);

    }

    @PostMapping("/login/thirdPart")
    @ResponseBody
    public R<Map> thirdPartLogin(@RequestBody User userInfo) {
        LambdaUpdateWrapper<User> wrapper = new LambdaUpdateWrapper<>();
         wrapper.eq(User::getUuid,userInfo.getUuid());
        User user = userService.getOne(wrapper);
        if(user==null){
            if(userInfo.getNickname()==null){
                userInfo.setNickname("anonymous");
            }
            userService.save(userInfo);
            Map<String, String> map = new HashMap<>();
            map.put("serverToken",JWTUtils.generateToken(userInfo));
            return R.success(map);
        }else {
            Map<String, String> map = new HashMap<>();
            map.put("serverToken",JWTUtils.generateToken(user));
            return R.success(map);
        }

    }


    @GetMapping("/token")
    public R<String> requestToken(){
        Long currentId = BaseContext.getCurrentId();
        User user = userService.getById(currentId);
        try {
            return R.success(FirebaseAuth.getInstance().createCustomToken(user.getUuid())) ;
        } catch (FirebaseAuthException e) {
            throw new CustomException("System error");
        }

    }


    @GetMapping("/currentUser")
    public R<User> getCurrentUser() {
        Long currentId = BaseContext.getCurrentId();
        User user = userService.getById(currentId);
        if (user==null){
            throw new CustomException("Login first");
        }
        user.setPassword(null);
        return R.success(user);
    }



    @GetMapping("/logout")
    @ResponseBody
    public R<String> logout( ) {
        return  R.success("Log Out");
    }

    @PostMapping("/signup")
    @ResponseBody
    public R<String> signup(@RequestBody User userInfo) {
        if(StringUtil.isNullOrEmpty(userInfo.getAvatar())){
            userInfo.setAvatar("http://35.189.24.208:8080/api/images/default.jpg");
        }
        userInfo.setPassword(PasswordUtils.hashPassword(userInfo.getPassword()));
        userInfo.setUuid(UUID.randomUUID().toString());
        userService.save(userInfo);
        return R.success("Sign up success");

    }

    @GetMapping("/username")
    @ResponseBody
    public R<String> usernameCheck(@RequestParam("userName") String userName) {

        MPJLambdaWrapper<User> wrapper = new MPJLambdaWrapper<>();
        wrapper.eq(User::getUsername,userName);
        User user = userService.getOne(wrapper);
        if (user == null) {
            return R.success("username is valid");
        }
        return R.error("username exist");
    }

    @PostMapping("/email")
    @ResponseBody
    public R<String> sendEmailByUsername(@RequestBody Map map) {
        String email = (String) map.get("email");
        String userName = (String) map.get("userName");
        userService.sendEmail(email, userName);
        return R.success("成功发送邮件");
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




    @GetMapping("/profile/{userId}")
    public R<UserDto> getProfile(@PathVariable("userId") Long userId) {
        Long currentUserId = BaseContext.getCurrentId();
        UserDto profile = userService.getProfile(userId);
        return R.success(profile);
    }

    @PutMapping("/profile")
    public R<String> updateProfile(@RequestBody User user) {
        if(user.getUserId()!=BaseContext.getCurrentId()){
            throw new CustomException("No right to access");
        }
     /*   long currentUserId = BaseContext.getCurrentId();
        UserDto profile = userService.getProfile(userId,currentUserId);*/
        return R.success("成功");
    }

    @GetMapping("/loves")
    public R<List<String>> getUserLovedPosts(){
/*        User user = userService.getById(BaseContext.getCurrentId());
        String loveList = user.getLoveList();
        try {
            List<String> res = objectMapper.readValue(loveList==null?"[]":loveList, new TypeReference<>() {
            });
            return R.success(res);
        } catch (JsonProcessingException e) {
            throw new CustomException("System error");
        }*/
        return null;
    }


    @GetMapping("/test/MQ")
    public R<String> getOnlineUsers(){
        log.info("111");
        rabbitTemplate.convertAndSend(MQConfig.SYSTEM_EXCHANGE,"C","111");
      return  null;
    }


}
