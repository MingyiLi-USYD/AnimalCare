package usyd.mingyi.animalcare.service.serviceImp;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.yulichang.query.MPJQueryWrapper;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import usyd.mingyi.animalcare.dto.FriendshipDto;
import usyd.mingyi.animalcare.mapper.FriendRequestMapper;
import usyd.mingyi.animalcare.mapper.FriendshipMapper;
import usyd.mingyi.animalcare.mapper.UserMapper;
import usyd.mingyi.animalcare.pojo.FriendRequest;
import usyd.mingyi.animalcare.pojo.Friendship;
import usyd.mingyi.animalcare.pojo.User;
import usyd.mingyi.animalcare.service.FriendshipService;
import usyd.mingyi.animalcare.service.RealTimeService;
import usyd.mingyi.animalcare.socketEntity.ServiceMessage;
import usyd.mingyi.animalcare.socketEntity.ServiceMessageType;

import java.util.List;

@Service
public class FriendshipServiceImp implements FriendshipService {

    @Autowired
    FriendshipMapper friendshipMapper;

    @Autowired
    FriendRequestMapper friendRequestMapper;

    @Autowired
    UserMapper userMapper;

    @Autowired
    RealTimeService realTimeService;


    @Override
    public List<FriendshipDto> getAllFriends(Long userId) {

        MPJLambdaWrapper<Friendship> query = new MPJLambdaWrapper<>();
        query.selectAll(Friendship.class)
                .selectAssociation(User.class, FriendshipDto::getFriendInfo)
                .leftJoin(User.class, User::getUserId, Friendship::getFriendId)
                .eq(Friendship::getMyId, userId);
        return friendshipMapper.selectJoinList(FriendshipDto.class, query);

    }


    @Override
    @Transactional
    public int checkFriendshipStatus(Long fromId, Long toId) {

        MPJLambdaWrapper<Friendship> query = new MPJLambdaWrapper<>();
        query.selectAll(Friendship.class)
                .eq(Friendship::getMyId, fromId)
                .eq(Friendship::getFriendId, toId);
        Friendship friendship = friendshipMapper.selectOne(query);
        if (friendship == null) {//证明还不是好友  现在判断是否有发送过好友请求
            MPJLambdaWrapper<FriendRequest> requestQuery = new MPJLambdaWrapper<>();
            requestQuery.selectAll(FriendRequest.class)
                    .eq(FriendRequest::getMyId, fromId)
                    .eq(FriendRequest::getFriendId, toId);
            FriendRequest friendRequest = friendRequestMapper.selectOne(requestQuery);
            if (friendRequest != null) {
                return 2;//表示已经发送了好友请求 等待同意 pending状态
            } else {
                return 3; //表示不是好友 并且重来没有发送过好友请求
            }
        } else {
            return 1; //表示已经是好友了
        }
    }

    @Override
    @Transactional
    public void deleteUser(Long fromId, Long toId) {
        deleteUserInFriendList(fromId, toId);
        deleteUserInFriendList(toId, fromId);
    }

    @Override
    @Transactional
    public void deleteUserSyncSocket(Long fromId, Long toId) {
        this.deleteUser(fromId,toId);
        realTimeService.remindFriends(new ServiceMessage(String.valueOf(fromId),System.currentTimeMillis(),String.valueOf(toId), ServiceMessageType.DELETE_FRIEND));
    }



    public void deleteUserInFriendList(Long userId, Long deleteUserId) {
        LambdaQueryWrapper<Friendship> query = new LambdaQueryWrapper<>();
        query.eq(Friendship::getMyId,userId).eq(Friendship::getFriendId,deleteUserId);
        friendshipMapper.delete(query);
    }

    public FriendshipDto getFriendshipByIds(Long userId, Long friendId){
        MPJLambdaWrapper<Friendship> query = new MPJLambdaWrapper<>();
        query.selectAll(Friendship.class)
                .selectAssociation(User.class,FriendshipDto::getFriendInfo)
                .leftJoin(User.class,User::getUserId,Friendship::getFriendId)
                .eq(Friendship::getMyId,userId)
                .eq(Friendship::getFriendId,friendId);
       return friendshipMapper.selectJoinOne(FriendshipDto.class, query);

    }

    @Override
    public Boolean isFriend(Long userId, Long friendId) {
       return friendshipMapper.selectCount
               (new LambdaQueryWrapper<Friendship>()
                       .eq(Friendship::getMyId,userId)
                       .eq(Friendship::getFriendId,friendId))>0;
    }


}
