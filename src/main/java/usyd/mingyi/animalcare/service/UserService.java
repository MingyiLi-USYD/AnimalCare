package usyd.mingyi.animalcare.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import usyd.mingyi.animalcare.dto.UserDto;
import usyd.mingyi.animalcare.mapper.UserMapper;
import usyd.mingyi.animalcare.pojo.User;


public interface UserService extends IService<User> {
    User queryUser(String username, String password);


    String queryPassword(String username);

    User queryUserByUsername(String username);

    void sendEmail(String email,String userName);

    UserDto getProfile(long targetUserId,long currentUserId);

    int updatePassword(String username,String password);

    User getBasicUserInfoById(Long id);
}
