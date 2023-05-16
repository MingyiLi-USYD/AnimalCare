package usyd.mingyi.animalcare.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import usyd.mingyi.animalcare.mapper.UserMapper;
import usyd.mingyi.animalcare.pojo.User;
import usyd.mingyi.animalcare.utils.Verification;

import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
public class UserServiceImp implements UserService {

    @Autowired
    UserMapper userMapper;

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    JavaMailSenderImpl mailSender;

    @Autowired
    RedisTemplate redisTemplate;

    @Override
    public User queryUser(String username, String password) {
        return userMapper.queryUser(username, password);
    }

    @Override
    public int addUser(User user) {
        return userMapper.addUser(user);
    }

    @Override
    public String queryPassword(String username) {
        return userMapper.queryPassword(username);
    }

    @Override
    public int updateUser(User user) {
        return userMapper.updateUser(user);
    }

    @Override
    public User queryUserByUsername(String username) {
        return userMapper.queryUserByUsername(username);
    }

    @Async
    public void sendEmail(String email, String userName) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        int i = new Random().nextInt(1000000);
        String code = String.format("%06d", i);
        mailMessage.setSubject("Verification Code");
        mailMessage.setText("This is your one time verification code :" + code + " [Valid in 5 Minutes]");
        mailMessage.setTo(email);
        mailMessage.setFrom("LMY741917776@gmail.com");
        mailSender.send(mailMessage);
        redisTemplate.opsForValue().set(userName, code, 300, TimeUnit.SECONDS);
        //Verification.putCode(userName, code);
    }

    @Override
    public User queryUserById(int userId) {
        return userMapper.queryUserById(userId);
    }

    @Override
    public User getProfile(int userId) {
        return userMapper.getProfile(userId);
    }

    @Override
    public int updatePassword(String username, String password) {
        return userMapper.updatePassword(username, password);
    }

}
