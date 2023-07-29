package usyd.mingyi.animalcare.service.serviceImp;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import usyd.mingyi.animalcare.dto.UserDto;

import usyd.mingyi.animalcare.mapper.UserMapper;
import usyd.mingyi.animalcare.pojo.Pet;
import usyd.mingyi.animalcare.pojo.Post;
import usyd.mingyi.animalcare.pojo.User;
import usyd.mingyi.animalcare.service.UserService;

import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
public class UserServiceImp extends ServiceImpl<UserMapper,User> implements UserService {

    @Autowired
    UserMapper userMapper;

    @Autowired
    JavaMailSenderImpl mailSender;

    @Autowired
    RedisTemplate redisTemplate;


    @Override
    public User getUserByUsername(String username) {
        MPJLambdaWrapper<User> query = new MPJLambdaWrapper<>();
        query.selectAll(User.class).eq(User::getUsername,username);
        return userMapper.selectOne(query);
    }

    @Override
    public String queryPassword(String username) {
        return userMapper.queryPassword(username);
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
    public UserDto getProfile(Long targetId) {
        MPJLambdaWrapper<User> query = new MPJLambdaWrapper<>();
        query.selectAll(User.class)
                .selectCollection(Post.class,UserDto::getPostList)
                .leftJoin(Post.class,Post::getUserId,User::getUserId)
                .selectCollection(Pet.class,UserDto::getPetList)
                .leftJoin(Pet.class,Pet::getUserId,User::getUserId)
                .eq(User::getUserId,targetId);
        UserDto userDto = userMapper.selectJoinOne(UserDto.class, query);
        System.out.println(userDto);
        return userDto;

    }

    @Override
    public int updatePassword(String username, String password) {
        return userMapper.updatePassword(username, password);
    }

    @Override
    public User getBasicUserInfoById(Long id) {
        MPJLambdaWrapper<User> wrapper = new MPJLambdaWrapper<>();
        wrapper.select(User::getNickname).select(User::getAvatar)
                .select(User::getUserId).select(User::getDescription).eq(User::getUserId,id);
        return userMapper.selectOne(wrapper);
    }

}
