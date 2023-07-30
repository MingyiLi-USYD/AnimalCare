package usyd.mingyi.animalcare.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import usyd.mingyi.animalcare.dto.UserDto;
import usyd.mingyi.animalcare.dto.UserInitDto;
import usyd.mingyi.animalcare.mapper.UserMapper;
import usyd.mingyi.animalcare.pojo.User;

import java.util.List;


public interface UserService extends IService<User> {
    User getUserByUsername(String username);


    String queryPassword(String username);

    void sendEmail(String email,String userName);

    UserDto getProfile(Long targetUserId);

    int updatePassword(String username,String password);

    User getBasicUserInfoById(Long id);

    UserInitDto initUserInfo(Long id);


}
