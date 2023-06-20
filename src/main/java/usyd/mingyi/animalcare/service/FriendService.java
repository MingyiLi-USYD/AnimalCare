package usyd.mingyi.animalcare.service;

import usyd.mingyi.animalcare.pojo.User;

import java.util.List;

public interface FriendService {

    int sendFriendRequest(long fromId,long toId,String msg);

    int acceptFriendRequest(long fromId,long toId);
    int rejectFriendRequest(long fromId,long toId);
    List<User> getAllFriends(long id);//根据用户id获取用户所有的friends
    List<User> getAllRequests(long id);//根据用户id获取用户所有的request
    int checkFriendshipStatus(long fromId, long toId);
    int deleteFromFriendList(long fromId,long toId);
}
