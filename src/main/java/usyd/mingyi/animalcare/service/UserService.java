package usyd.mingyi.animalcare.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import usyd.mingyi.animalcare.mapper.UserMapper;
import usyd.mingyi.animalcare.pojo.User;


public interface UserService extends IService<User> {
    User queryUser(String username, String password);

    int addUser(User user);

    String queryPassword(String username);

    int updateUser(User user);

    User queryUserByUsername(String username);

    public  void sendEmail(String email,String userName);

    User queryUserById(long userId);

    User getProfile(long userId);

    int updatePassword(String username,String password);
}
