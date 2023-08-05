package usyd.mingyi.animalcare.controller;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import io.netty.util.internal.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import usyd.mingyi.animalcare.annotation.Status;
import usyd.mingyi.animalcare.common.CustomException;
import usyd.mingyi.animalcare.common.R;
import usyd.mingyi.animalcare.component.ClientCache;
import usyd.mingyi.animalcare.dto.UserDto;
import usyd.mingyi.animalcare.dto.UserInitDto;
import usyd.mingyi.animalcare.pojo.Subscription;
import usyd.mingyi.animalcare.pojo.User;
import usyd.mingyi.animalcare.service.SubscriptionService;
import usyd.mingyi.animalcare.service.UserService;
import usyd.mingyi.animalcare.utils.*;

import javax.validation.constraints.Pattern;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


@RestController
@Slf4j
@Validated
public class UserController {
    @Autowired
    UserService userService;

    @Autowired
    SubscriptionService subscriptionService;
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
            if (!PasswordUtils.verifyPassword(password, user.getPassword())) {
                throw new CustomException("Password Error");
            }
        }

        Map<String, String> map = new HashMap<>();
        map.put("serverToken", JWTUtils.generateToken(user));
        map.put("firebaseToken", JWTUtils.generateFirebaseToken(String.valueOf(user.getUserId())));
        return R.success(map);

    }

    @PostMapping("/login/thirdPart")
    @ResponseBody
    public R<Map> thirdPartLogin(@RequestBody User userInfo) {
        LambdaUpdateWrapper<User> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(User::getUuid, userInfo.getUuid());
        User user = userService.getOne(wrapper);
        if (user == null) {
            if (userInfo.getNickname() == null) {
                userInfo.setNickname("anonymous");
            }
            userInfo.setPassword("123456");
            userInfo.setRole("user");
            userService.save(userInfo);
            Map<String, String> map = new HashMap<>();
            map.put("serverToken", JWTUtils.generateToken(userInfo));
            return R.success(map);
        } else {
            Map<String, String> map = new HashMap<>();
            map.put("serverToken", JWTUtils.generateToken(user));
            return R.success(map);
        }

    }


    @GetMapping("/token")
    public R<String> requestToken() {
        Long currentId = BaseContext.getCurrentId();
        User user = userService.getById(currentId);
        try {
            return R.success(FirebaseAuth.getInstance().createCustomToken(user.getUuid()));
        } catch (FirebaseAuthException e) {
            throw new CustomException("System error");
        }

    }


    @GetMapping("/currentUser")
    public R<User> getCurrentUser() {
        Long currentId = BaseContext.getCurrentId();
        User user = userService.getById(currentId);
        if (user == null) {
            throw new CustomException("Login first");
        }
        return R.success(user);
    }


    @GetMapping("/logout")
    @ResponseBody
    public R<String> logout() {
        return R.success("Log Out");
    }

    @PostMapping("/signup")
    @ResponseBody
    public R<String> signup(@RequestBody User userInfo) {
        if (StringUtil.isNullOrEmpty(userInfo.getAvatar())) {
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
        wrapper.eq(User::getUsername, userName);
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
        if (!user.getUserId().equals(BaseContext.getCurrentId()) ) {
            throw new CustomException("No right to access");
        }
     /*   long currentUserId = BaseContext.getCurrentId();
        UserDto profile = userService.getProfile(userId,currentUserId);*/
        return R.success("成功");
    }

    @GetMapping("/user/init")
    public R<UserInitDto> initUserInfo() {
        UserInitDto userInitDto = userService.initUserInfo(BaseContext.getCurrentId());
        return R.success(userInitDto);
    }

    @GetMapping("/users")
    public R<Page<User>> getAllUser(@RequestParam("current") Long current
            , @RequestParam("size") Long size, @RequestParam("keywords") String keywords) {
        MPJLambdaWrapper<User> query = new MPJLambdaWrapper<>();
        query.selectAll(User.class).
                like(keywords != null && !keywords.isEmpty(), User::getUsername, keywords);
        Page<User> page = userService.page(new Page<>(current, size), query);
        return R.success(page);
    }

    @GetMapping("/changeUser/{userId}")
    public R<String> changeUser(@PathVariable("userId") Long userId,
                                @RequestParam(value = "role", required = false)
                                @Pattern(regexp = "Root|Admin|SuperAdmin|User") String role,
                                @RequestParam(value = "status", required = false)
                                @Status Byte status) {


        LambdaUpdateWrapper<User> update = new LambdaUpdateWrapper<>();
        update.set(role != null, User::getRole, role)
                .set(status != null, User::getStatus, status)
                .eq(User::getUserId, userId);

        userService.update(update);
        return R.success("Success");
    }

    @PostMapping("/subscribe/{id}")
    public R<String> subscribeUser(@PathVariable("id") Long userId) {
        Subscription subscription = new Subscription();
        subscription.setMyId(BaseContext.getCurrentId());
        subscription.setSubscribedUserId(userId);
        subscriptionService.save(subscription);
        return R.success("Subscribe success");
    }
    @DeleteMapping("/subscribe/{id}")
    public R<String> unsubscribeUser(@PathVariable("id") Long userId) {
        LambdaUpdateWrapper<Subscription> update = new LambdaUpdateWrapper<>();
         update.eq(Subscription::getMyId,BaseContext.getCurrentId())
                         .eq(Subscription::getSubscribedUserId,userId);
        if (!subscriptionService.remove(update)) {
            throw new CustomException("Never subscribe before");
        }

        return R.success("Unsubscribe success");
    }

}
