package usyd.mingyi.animalcare.service.serviceImp;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import usyd.mingyi.animalcare.common.CustomException;
import usyd.mingyi.animalcare.dto.FriendRequestDto;
import usyd.mingyi.animalcare.dto.UserDto;
import usyd.mingyi.animalcare.mapper.FriendRequestMapper;
import usyd.mingyi.animalcare.mapper.FriendshipMapper;
import usyd.mingyi.animalcare.mapper.UserMapper;
import usyd.mingyi.animalcare.pojo.FriendRequest;
import usyd.mingyi.animalcare.pojo.Friendship;
import usyd.mingyi.animalcare.pojo.User;
import usyd.mingyi.animalcare.service.FriendRequestService;
import usyd.mingyi.animalcare.utils.QueryUtils;

import java.util.*;


@Service
@Slf4j
public class FriendRequestServiceImp extends ServiceImpl<FriendRequestMapper, FriendRequest> implements FriendRequestService {

    @Autowired
    FriendRequestMapper friendRequestMapper;
    @Autowired
    FriendshipMapper friendshipMapper;
    @Autowired
    UserMapper userMapper;


    @Override
    @Transactional
    public void sendRequest(Long userId, Long targetUserId, String msg) {
        //当发现对方之前已经发生给好友请求 并且还在你的好友请求记录中 立刻成为好友
        MPJLambdaWrapper<FriendRequest> query = QueryUtils.generateFriendRequestQuery(userId, targetUserId);
        FriendRequest existRequest = friendRequestMapper.selectOne(query);
        if(existRequest!=null){
            friendRequestMapper.deleteById(existRequest.getRequestId());
            addUserToFriendList(userId,targetUserId);
            addUserToFriendList(targetUserId,userId);
            return;
        }
        FriendRequest friendRequest = new FriendRequest();
        friendRequest.setMyId(userId);
        friendRequest.setFriendId(targetUserId);
        friendRequest.setMsg(msg);
        friendRequestMapper.insert(friendRequest);

    }

    @Override
    @Transactional
    public void approveRequest(Long userId, Long approvedUserId) {
        MPJLambdaWrapper<FriendRequest> query = QueryUtils.generateFriendRequestQuery(userId, approvedUserId);
        FriendRequest friendRequest = friendRequestMapper.selectOne(query);
        if(friendRequest==null){
            //没有发现此请求记录
            throw new CustomException("Not found such friend request in your request list");
        }else {
            //存在好友请求  删除请求并且互相添加为好友
            friendRequestMapper.deleteById(friendRequest.getRequestId());
            addUserToFriendList(userId,approvedUserId);
            addUserToFriendList(approvedUserId,userId);

        }

    }

    @Override
    public void addUserToFriendList(Long userId, Long approvedUserId) {
        Friendship friendship = new Friendship();
        friendship.setMyId(userId);
        friendship.setFriendshipId(approvedUserId);
        friendshipMapper.insert(friendship);
    }

    @Override
    @Transactional
    public void rejectRequest(Long userId, Long targetUserId) {
        MPJLambdaWrapper<FriendRequest> query = QueryUtils.generateFriendRequestQuery(userId, targetUserId);
        FriendRequest existRequest = friendRequestMapper.selectOne(query);
        if(existRequest==null){
            throw new CustomException("Not found such friend request in your request list");
        }else {
            friendRequestMapper.deleteById(existRequest.getRequestId());
        }

    }





    @Override
    @Transactional
    public List<FriendRequestDto> getAllRequest(Long userId) {
        MPJLambdaWrapper<FriendRequest> query = new MPJLambdaWrapper<>();
        query.selectAll(FriendRequest.class)
                .selectAssociation(User.class, FriendRequestDto::getFriendInfo)
                .leftJoin(User.class, User::getUserId, FriendRequest::getFriendId)
                .eq(FriendRequest::getMyId, userId);
        List<FriendRequestDto> friendRequestDtos = friendRequestMapper.selectJoinList(FriendRequestDto.class, query);
        System.out.println(friendRequestDtos);
        return friendRequestDtos;

    }

    @Override
    public UserDto getRequestById(Long userId, Long target) {
        return null;

    }


}
