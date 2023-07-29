package usyd.mingyi.animalcare.service.serviceImp;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import usyd.mingyi.animalcare.dto.FriendRequestDto;
import usyd.mingyi.animalcare.dto.UserDto;
import usyd.mingyi.animalcare.mapper.FriendRequestMapper;
import usyd.mingyi.animalcare.mapper.UserMapper;
import usyd.mingyi.animalcare.pojo.FriendRequest;
import usyd.mingyi.animalcare.pojo.User;
import usyd.mingyi.animalcare.service.FriendRequestService;
import java.util.*;


@Service
@Slf4j
public class FriendRequestServiceImp extends ServiceImpl<FriendRequestMapper, FriendRequest> implements FriendRequestService {

    @Autowired
    FriendRequestMapper friendRequestMapper;
    @Autowired
    UserMapper userMapper;
    @Autowired
    ObjectMapper objectMapper;


    @Override
    @Transactional
    public void sendRequest(Long fromId, Long toId, String msg) {
    }

    @Override
    @Transactional
    public void approveRequest(Long userId, Long approvedUserId) {


    }

    @Override
    public void addUserToFriendList(Long userId, Long approvedUserId) {

    }

    @Override
    @Transactional
    public void rejectRequest(Long userId, Long approvedUserId) {

    }


    @Override
    @Transactional
    public void deleteRequestInList(Long userId, Long rejectUserId) {

    }


    @Override
    @Transactional
    public List<FriendRequestDto> getAllRequest(Long userId) {
        MPJLambdaWrapper<FriendRequest> query = new MPJLambdaWrapper<>();
        query.selectAll(FriendRequest.class)
                .selectAssociation(User.class,FriendRequestDto::getFriendInfo)
                .leftJoin(User.class,User::getUserId,FriendRequest::getFriendId)
                .eq(FriendRequest::getMyId,userId);
        List<FriendRequestDto> friendRequestDtos = friendRequestMapper.selectJoinList(FriendRequestDto.class, query);
        System.out.println(friendRequestDtos);
        return  friendRequestDtos;

    }

    @Override
    public UserDto getRequestById(Long userId, Long target) {
        return  null;

    }




}
