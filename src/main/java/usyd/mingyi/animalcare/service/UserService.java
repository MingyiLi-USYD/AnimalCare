package usyd.mingyi.animalcare.service;

import com.baomidou.mybatisplus.extension.service.IService;
import usyd.mingyi.animalcare.dto.UserDto;
import usyd.mingyi.animalcare.pojo.User;


public interface UserService extends IService<User> {
    User getUserByUsername(String username);


    String queryPassword(String username);

    void sendEmail(String email,String userName);

    UserDto getProfile(Long targetUserId);

    int updatePassword(String username,String password);

    User getBasicUserInfoById(Long id);

    UserDto initUserInfo(Long id);


}
