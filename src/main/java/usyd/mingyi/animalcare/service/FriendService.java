package usyd.mingyi.animalcare.service;

import usyd.mingyi.animalcare.pojo.User;

import java.util.List;

public interface FriendService {

    int sendFriendRequest(int fromId,int toId);

    int acceptFriendRequest(int fromId,int toId);
    int rejectFriendRequest(int fromId,int toId);
    List<User> getAllFriends(int id);//根据用户id获取用户所有的friends
    List<User> getAllRequests(int id);//根据用户id获取用户所有的request
    int checkFriendshipStatus(int fromId, int toId);
    int deleteFromFriendList(int fromId,int toId);
}
