package usyd.mingyi.animalcare.service;

import usyd.mingyi.animalcare.dto.FriendshipDto;
import usyd.mingyi.animalcare.pojo.User;

import java.util.List;

public interface FriendshipService  {

    List<FriendshipDto> getAllFriends(Long id);//根据用户id获取用户所有的friends
    int checkFriendshipStatus(long fromId, long toId);
    void deleteUser(long fromId,long toId);
    User getFriendSync(long id);
}
