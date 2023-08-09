package usyd.mingyi.animalcare.service.serviceImp;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import usyd.mingyi.animalcare.common.CustomException;
import usyd.mingyi.animalcare.dto.FriendRequestDto;
import usyd.mingyi.animalcare.dto.FriendshipDto;
import usyd.mingyi.animalcare.dto.UserDto;
import usyd.mingyi.animalcare.dto.UserInitDto;
import usyd.mingyi.animalcare.mapper.SubscriptionMapper;
import usyd.mingyi.animalcare.mapper.UserMapper;
import usyd.mingyi.animalcare.pojo.Pet;
import usyd.mingyi.animalcare.pojo.Post;
import usyd.mingyi.animalcare.pojo.Subscription;
import usyd.mingyi.animalcare.pojo.User;
import usyd.mingyi.animalcare.service.*;
import usyd.mingyi.animalcare.utils.BaseContext;

import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class UserServiceImp extends ServiceImpl<UserMapper,User> implements UserService {

    @Autowired
    UserMapper userMapper;

    @Autowired
    FriendshipService friendshipService;

    @Autowired
    FriendRequestService friendRequestService;

    @Autowired
    SubscriptionService subscriptionService;

    @Autowired
    PostService postService;

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
                .leftJoin(Post.class, on->on.eq(Post::getUserId,User::getUserId)
                .eq(!BaseContext.getCurrentId().equals(targetId),Post::getVisible,true))
                .selectCollection(Pet.class,UserDto::getPetList)
                .leftJoin(Pet.class,on->on.eq(Pet::getUserId,User::getUserId)
                 .eq(!BaseContext.getCurrentId().equals(targetId),Pet::getPetVisible,true))
                .eq(User::getUserId,targetId);

        UserDto userDto = userMapper.selectJoinOne(UserDto.class, query);


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

    @Override
    public UserInitDto initUserInfo(Long id) {
        UserInitDto userInitDto = new UserInitDto();
        UserDto profile = getProfile(id);
        if(profile==null){
            throw new CustomException("Not found this user");
        }
        List<FriendshipDto> allFriends = friendshipService.getAllFriends(id);
        List<FriendRequestDto> allRequest = friendRequestService.getAllRequest(id);
        BeanUtils.copyProperties(profile, userInitDto);
        userInitDto.setFriendshipDtoList(allFriends);
        userInitDto.setFriendRequestDtoList(allRequest);
        userInitDto.setLoveIdList(postService.getAllLovedPostsIdInString(id));
        userInitDto.setSubscribedUserIdList(subscriptionService.getAllSubscribes(id));

        return userInitDto;
    }



}
