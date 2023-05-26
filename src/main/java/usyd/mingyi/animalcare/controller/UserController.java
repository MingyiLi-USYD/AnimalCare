package usyd.mingyi.animalcare.controller;

import io.netty.util.internal.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import usyd.mingyi.animalcare.common.CustomException;
import usyd.mingyi.animalcare.common.R;
import usyd.mingyi.animalcare.config.ProjectProperties;
import usyd.mingyi.animalcare.pojo.User;
import usyd.mingyi.animalcare.service.UserService;
import usyd.mingyi.animalcare.utils.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;



@RestController
@Slf4j
public class UserController {
    @Autowired
    UserService userService;
    @Autowired
    RedisTemplate redisTemplate;
    @Autowired
    RestTemplate restTemplate;
    @Autowired
    ProjectProperties projectProperties;

    //Two main ways to receive data from frontend map and pojo, we plan to use pojo to receive data for better maintain in future
    @PostMapping("/login")
    @ResponseBody

    public R<String> login(@RequestBody User userInfo) {

        log.info("登录");
        String username = userInfo.getUserName();
        String password = userInfo.getPassword();
        String encryptedPassword = userService.queryPassword(username);

        if (encryptedPassword == null) {
            throw new CustomException("No such user");
        } else {
            String decode = JasyptEncryptorUtils.decode(encryptedPassword);
            if (!decode.equals(password)) {
                throw new CustomException("Password Error");
            }
        }

        User user = userService.queryUser(username, encryptedPassword);

        if (user != null) {

            return R.success(JWTUtils.generateToken(user));

        } else {
            throw new CustomException("Password Error");
        }
    }

    @GetMapping("/currentUser")
    public R<User> getCurrentUser() {
        Long currentId = BaseContext.getCurrentId();
        User byId = userService.getById(currentId);
        System.out.println(byId);
        log.info(byId.toString());
        User profile = userService.getProfile(currentId);
        if (profile==null){
            throw new CustomException("Login first");
        }
        return R.success(profile);
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
        userInfo.setPassword(JasyptEncryptorUtils.encode(userInfo.getPassword()));
        userInfo.setUuid(UUID.randomUUID().toString());
        String randomNickname = RandomUtils.getRandomNickname(restTemplate);
        userInfo.setNickName(randomNickname);
        userService.save(userInfo);
        return R.success("Sign up success");

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

    @PostMapping("/edit")
    @ResponseBody
    public ResponseEntity<Object> updateUserInfo(@RequestBody User userInfo, HttpServletRequest request) {
        String auth = request.getHeader("auth");
        String userName = JWTUtils.getTokenInfo(auth).getClaim("userName").asString();

        String fileDiskLocation = projectProperties.fileDiskLocation;
        String projectPrefix = projectProperties.projectPrefix;
        long id =  BaseContext.getCurrentId();
        userInfo.setId(id);
        String avatarUrl = userInfo.getAvatar();
        if (!StringUtil.isNullOrEmpty(avatarUrl) && ImageUtil.checkImage(avatarUrl)) {
            //更改照片
            String suffix = ImageUtil.getSuffix(avatarUrl);
            String data = ImageUtil.getData(avatarUrl);
            String tempFileName = UUID.randomUUID().toString() + suffix; //文件名
            String path = fileDiskLocation + userName; //文件路径
            try {
                ImageUtil.convertBase64ToFile(data, path, tempFileName);
                userInfo.setAvatar(projectPrefix + userName + "/" + tempFileName);
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

/*    @PostMapping("/android/edit")
    @ResponseBody
    public ResponseEntity<Object> updateUserInfoInAndroid(@RequestParam(value = "avatar",required = false) MultipartFile avatar,
                                                          @RequestParam("nickName") String nickName,
                                                          @RequestParam("description") String description,
                                                          HttpSession session) {
        String fileDiskLocation = projectProperties.fileDiskLocation;
        ;
        String projectPrefix = projectProperties.projectPrefix;
        long id = (long) session.getAttribute("id");
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
            userInfo.setAvatar(projectPrefix + userName + "/" + tempFileName);
        }catch (NullPointerException | IOException e) {
            e.printStackTrace();
        }
        userService.updateUser(userInfo);
        return new ResponseEntity<>(ResultData.success("Update success"), HttpStatus.OK);
    }*/


}
