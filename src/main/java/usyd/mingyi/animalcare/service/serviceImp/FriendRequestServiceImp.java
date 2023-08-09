package usyd.mingyi.animalcare.service.serviceImp;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import usyd.mingyi.animalcare.common.CustomException;
import usyd.mingyi.animalcare.dto.FriendRequestDto;
import usyd.mingyi.animalcare.dto.FriendshipDto;
import usyd.mingyi.animalcare.dto.UserDto;
import usyd.mingyi.animalcare.mapper.FriendRequestMapper;
import usyd.mingyi.animalcare.mapper.FriendshipMapper;
import usyd.mingyi.animalcare.mapper.UserMapper;
import usyd.mingyi.animalcare.pojo.FriendRequest;
import usyd.mingyi.animalcare.pojo.Friendship;
import usyd.mingyi.animalcare.pojo.User;
import usyd.mingyi.animalcare.service.FriendRequestService;
import usyd.mingyi.animalcare.service.FriendshipService;
import usyd.mingyi.animalcare.service.RealTimeService;
import usyd.mingyi.animalcare.socketEntity.ServiceMessage;
import usyd.mingyi.animalcare.utils.QueryUtils;

import java.util.List;

import static usyd.mingyi.animalcare.socketEntity.ServiceMessageType.*;


@Service
@Slf4j
public class FriendRequestServiceImp extends ServiceImpl<FriendRequestMapper, FriendRequest> implements FriendRequestService {

    @Autowired
    FriendRequestMapper friendRequestMapper;
    @Autowired
    FriendshipMapper friendshipMapper;
    @Autowired
    UserMapper userMapper;

    @Autowired
    RealTimeService realTimeService;

    @Autowired
    FriendshipService friendshipService;



    @Override
    @Transactional
    public void sendRequest(Long userId, Long targetUserId, String msg) {
        //当发现对方之前已经发生给好友请求 并且还在你的好友请求记录中 立刻成为好友
        MPJLambdaWrapper<FriendRequest> query = QueryUtils.generateFriendRequestQuery(userId, targetUserId);
        FriendRequest existRequest = friendRequestMapper.selectOne(query);
        if (existRequest != null) {
            friendRequestMapper.deleteById(existRequest.getRequestId());
            addUserToFriendList(userId, targetUserId);
            addUserToFriendList(targetUserId, userId);
            return;
        }
        FriendRequest friendRequest = new FriendRequest();
        friendRequest.setMyId(userId);
        friendRequest.setFriendId(targetUserId);
        friendRequest.setMsg(msg);
        friendRequestMapper.insert(friendRequest);
    }

    @Transactional
    @Override
    public void sendRequestSyncSocket(Long fromId, Long toId, String msg) {
        this.sendRequest(fromId, toId, msg);
        realTimeService.remindFriends(
                new ServiceMessage(String.valueOf(fromId), System.currentTimeMillis(), String.valueOf(toId), ADD_FRIEND)
        );
    }


    @Override
    @Transactional
    public void approveRequest(Long userId, Long approvedUserId) {
        MPJLambdaWrapper<FriendRequest> query = QueryUtils.generateFriendRequestQuery(userId, approvedUserId);
        FriendRequest friendRequest = friendRequestMapper.selectOne(query);
        if (friendRequest == null) {
            //没有发现此请求记录
            throw new CustomException("Not found such friend request in your request list");
        } else {
            //存在好友请求  删除请求并且互相添加为好友
            friendRequestMapper.deleteById(friendRequest.getRequestId());
            addUserToFriendList(userId, approvedUserId);
            addUserToFriendList(approvedUserId, userId);
        }

    }

    @Transactional
    @Override
    public FriendshipDto approveRequestAndGetSyncSocket(Long userId, Long approvedUserId){
        this.approveRequest(userId,approvedUserId);
        realTimeService.remindFriends(new ServiceMessage(String.valueOf(userId),System.currentTimeMillis(),String.valueOf(approvedUserId),AGREE_ADD_FRIEND));
        return friendshipService.getFriendshipByIds(userId,approvedUserId);
    }




    @Override
    public void addUserToFriendList(Long userId, Long approvedUserId) {
        Friendship friendship = new Friendship();
        friendship.setMyId(userId);
        friendship.setFriendId(approvedUserId);
        friendshipMapper.insert(friendship);
    }

    @Override
    @Transactional
    public void rejectRequest(Long userId, Long targetUserId) {
        System.out.println(userId);
        System.out.println(targetUserId);
        MPJLambdaWrapper<FriendRequest> query = QueryUtils.generateFriendRequestQuery(userId, targetUserId);
        FriendRequest existRequest = friendRequestMapper.selectOne(query);
        if (existRequest == null) {
            throw new CustomException("Not found such friend request in your request list");
        } else {
            friendRequestMapper.deleteById(existRequest.getRequestId());
        }

    }

    @Override
    @Transactional
    public void rejectRequestSyncSocket(Long userId, Long rejectUserId){
         this.rejectRequest(userId,rejectUserId);
        realTimeService.remindFriends(new ServiceMessage(String.valueOf(userId),System.currentTimeMillis(),String.valueOf(rejectUserId),REJECT_ADD_FRIEND));
    }

    @Override
    @Transactional
    public List<FriendRequestDto> getAllRequest(Long userId) {
        MPJLambdaWrapper<FriendRequest> query = new MPJLambdaWrapper<>();
        query.selectAll(FriendRequest.class)
                .selectAssociation(User.class, FriendRequestDto::getFriendInfo)
                .leftJoin(User.class, User::getUserId, FriendRequest::getMyId)
                .eq(FriendRequest::getFriendId, userId);
        List<FriendRequestDto> friendRequestDtos = friendRequestMapper.selectJoinList(FriendRequestDto.class, query);
        System.out.println(friendRequestDtos);
        return friendRequestDtos;

    }

    @Override
    public UserDto getRequestById(Long userId, Long target) {
        return null;
    }


}
