package usyd.mingyi.animalcare.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import usyd.mingyi.animalcare.mapper.FriendMapper;
import usyd.mingyi.animalcare.pojo.User;

import java.util.List;

@Service
public class FriendServiceImp implements FriendService {
    @Autowired
    FriendMapper friendMapper;

    @Override
    @Transactional
    public int sendFriendRequest(int fromId, int toId) {
        // 返回值 0失败 1成功 2之前已经收到过对方请求直接互为好友
        int i = friendMapper.checkRequestReverse(fromId, toId);
        if (i > 0) {
            //直接添加成功
            friendMapper.addToFriendList(fromId, toId);
            friendMapper.addToFriendList(toId, fromId);
            //删除无效请求
            friendMapper.deleteFriendRequest(fromId, toId);
            friendMapper.deleteFriendRequest(toId, fromId);
            return 2;
        }
        //确定是否已经是朋友
        if (friendMapper.isFriend(fromId, toId)) return -1;
        //当没有重复请求时
        if (friendMapper.checkExistRequest(fromId, toId) == 0 && friendMapper.sendFriendRequest(fromId, toId) > 0) {//发出好友申请

            return 1;
        } else {
            return 0;
        }
    }

    @Override
    @Transactional
    public int acceptFriendRequest(int fromId, int toId) {
        if (friendMapper.checkExistRequest(toId, fromId) > 0 && !friendMapper.isFriend(fromId, toId)) {
            friendMapper.addToFriendList(fromId, toId);
            friendMapper.addToFriendList(toId, fromId);
            friendMapper.deleteFriendRequest(fromId, toId);
            friendMapper.deleteFriendRequest(toId, fromId);
            return 1;
        } else {
            return 0;
        }


    }

    @Override
    public int rejectFriendRequest(int fromId, int toId) {
        return friendMapper.deleteFriendRequest(toId, fromId);
    }

    @Override
    public List<User> getAllFriends(int id) {
        return friendMapper.getAllFriends(id);
    }

    @Override
    public List<User> getAllRequests(int id) {
        return friendMapper.getAllRequests(id);
    }

    @Override
    @Transactional
    public int checkFriendshipStatus(int fromId, int toId) {
        // has already been friend
        if (friendMapper.isFriend(fromId, toId)) return 1;
        // has already sent friend request
        if (friendMapper.checkExistRequest(fromId, toId) > 0) return 0;
        // has neither been friend nor sent friend request
        return -1;
    }

    @Override
    @Transactional
    public int deleteFromFriendList(int fromId, int toId) {
        int delete = friendMapper.deleteFromFriendList(fromId, toId);
        int deleteReverse = friendMapper.deleteFromFriendList(toId, fromId);
        if (delete == 1 && deleteReverse == 1) {
            return 1;
        } else {
            return -1;
        }
    }


}
