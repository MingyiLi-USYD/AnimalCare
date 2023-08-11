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
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import usyd.mingyi.animalcare.annotation.Status;
import usyd.mingyi.animalcare.common.CustomException;
import usyd.mingyi.animalcare.common.R;
import usyd.mingyi.animalcare.component.ClientCache;
import usyd.mingyi.animalcare.dto.UserDto;
import usyd.mingyi.animalcare.pojo.Subscription;
import usyd.mingyi.animalcare.pojo.User;
import usyd.mingyi.animalcare.service.SubscriptionService;
import usyd.mingyi.animalcare.service.UserService;
import usyd.mingyi.animalcare.utils.BaseContext;
import usyd.mingyi.animalcare.utils.JWTUtils;
import usyd.mingyi.animalcare.utils.PasswordUtils;

import javax.validation.constraints.Email;
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
    RedisTemplate<String, Object> redisTemplate;
    @Autowired
    RabbitTemplate rabbitTemplate;
    @Autowired
    ClientCache clientCache;
    @Autowired
    ObjectMapper objectMapper;

    //Two main ways to receive data from frontend map and pojo, we plan to use pojo to receive data for better maintain in future
    @PostMapping("/login")
    public R<Map<String, String>> emailLogin(@RequestBody @Validated User loginUser) {

        User user = userService.getUserByUsername(loginUser.getUsername());
        if (user == null) {
            throw new CustomException("No such user");
        }
        if (!PasswordUtils.verifyPassword(loginUser.getPassword(), user.getPassword())) {
            throw new CustomException("Password Error");
        }

        Map<String, String> map = new HashMap<>();
        map.put("serverToken", JWTUtils.generateToken(user));
        map.put("firebaseToken", JWTUtils.generateFirebaseToken(String.valueOf(user.getUserId())));
        return R.success(map);

    }
    @PostMapping("/signup")
    public R<String> signup(@RequestBody @Validated User userInfo) {
        User user = generateBasicUserInfo(userInfo);
        String password = user.getPassword();
        user.setPassword(PasswordUtils.hashPassword(password));
        userService.save(user);
        //邮件通知注册成功
        return R.success("Sign up success");
    }

    private User generateBasicUserInfo(User user){
        if(user.getAvatar()==null){
            user.setAvatar("https://robohash.org/"+user.hashCode());
        }
        if(user.getNickname()==null){
            user.setAvatar("User"+user.hashCode());
        }
        if(user.getPassword()==null){
            UUID uuid = UUID.randomUUID();
            String password = uuid.toString().replace("-", "").substring(0, 10);
            user.setPassword(password);
        }
        if(user.getUuid()==null){
            user.setUuid(UUID.randomUUID().toString());
        }
        user.setRole("User");
        user.setStatus((byte)1);
        return user;
    }


    @PostMapping("/login/thirdPart")
    public R<Map<String, String>> thirdPartLogin(@RequestBody User userInfo) {

        LambdaUpdateWrapper<User> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(User::getUuid, userInfo.getUuid());
        User dbUser = userService.getOne(wrapper);
        Map<String, String> map = new HashMap<>();
        if (dbUser == null) {
            User user = generateBasicUserInfo(userInfo);
            String password = user.getPassword();
            user.setPassword(PasswordUtils.hashPassword(password));
            userService.save(user);
            //发邮件通知注册成功
            map.put("serverToken", JWTUtils.generateToken(userInfo));
            return R.success(map);
        }
        if(!dbUser.getUsername().equals(userInfo.getUsername())){
            throw new CustomException("Userinfo not match");
        }
            //这个表示已经注册过 直接登录
            map.put("serverToken", JWTUtils.generateToken(dbUser));
            return R.success(map);
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
    public R<String> logout() {
        return R.success("Log Out");
    }


    @GetMapping("/username")
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
    public R<String> sendEmailByUsername(@RequestBody Map map) {
        String email = (String) map.get("email");
        String userName = (String) map.get("userName");
        userService.sendEmail(email, userName);
        return R.success("成功发送邮件");
    }

    @PostMapping("/validate")
    public ResponseEntity<Object> validateCode(@RequestBody Map map) {
        return null;
    }


    @GetMapping("/profile/{userId}")
    public R<UserDto> getProfile(@PathVariable("userId") Long userId) {
        //Long currentUserId = BaseContext.getCurrentId();
        UserDto profile = userService.getProfile(userId);
        return R.success(profile);
    }

    @PutMapping("/profile")
    public R<String> updateProfile(@RequestBody User user) {
        user.setUserId(BaseContext.getCurrentId());
        user.setRole(null);
        userService.updateById(user);
        return R.success("Success");
    }

    @GetMapping("/user/init")
    public R<UserDto> initUserInfo() {
        UserDto userInitDto = userService.initUserInfo(BaseContext.getCurrentId());
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
        Long currentId = BaseContext.getCurrentId();
        if (currentId.equals(userId)) {
            return R.error("Can not subscribe yourself");
        }
        Subscription subscription = new Subscription();
        subscription.setMyId(currentId);
        subscription.setSubscribedUserId(userId);
        subscriptionService.save(subscription);
        return R.success("Subscribe success");
    }

    @DeleteMapping("/subscribe/{id}")
    public R<String> unsubscribeUser(@PathVariable("id") Long userId) {
        Long currentId = BaseContext.getCurrentId();
        if (currentId.equals(userId)) {
            return R.error("Can not unsubscribe yourself");
        }

        LambdaUpdateWrapper<Subscription> update = new LambdaUpdateWrapper<>();
        update.eq(Subscription::getMyId, currentId)
                .eq(Subscription::getSubscribedUserId, userId);
        if (!subscriptionService.remove(update)) {
            throw new CustomException("Never subscribe before");
        }

        return R.success("Unsubscribe success");
    }

}
