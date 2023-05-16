package usyd.mingyi.animalcare.service;

import org.springframework.stereotype.Service;
import usyd.mingyi.animalcare.pojo.Post;
import usyd.mingyi.animalcare.pojo.User;

import java.util.List;


public interface UserService {
    User queryUser(String username, String password);

    int addUser(User user);

    String queryPassword(String username);

    int updateUser(User user);

    User queryUserByUsername(String username);

    public  void sendEmail(String email,String userName);

    User queryUserById(int userId);

    User getProfile(int userId);

    int updatePassword(String username,String password);
}
