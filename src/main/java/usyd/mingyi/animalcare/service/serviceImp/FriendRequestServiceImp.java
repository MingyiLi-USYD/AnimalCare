package usyd.mingyi.animalcare.service.serviceImp;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import usyd.mingyi.animalcare.common.CustomException;
import usyd.mingyi.animalcare.dto.UserDto;
import usyd.mingyi.animalcare.mapper.FriendRequestMapper;
import usyd.mingyi.animalcare.mapper.UserMapper;
import usyd.mingyi.animalcare.pojo.FriendRequest;
import usyd.mingyi.animalcare.pojo.User;
import usyd.mingyi.animalcare.service.FriendRequestService;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

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

        MPJLambdaWrapper<FriendRequest> wrapper = new MPJLambdaWrapper<>();
        wrapper.eq(FriendRequest::getUserId, toId);
        FriendRequest friendRequest = friendRequestMapper.selectOne(wrapper);
        try {
        if(friendRequest==null){
            Map<String, String> requestList = new HashMap<>();
            requestList.put(String.valueOf(fromId),msg);
            FriendRequest newFriendRequest = new FriendRequest();
            String stringRequestList = objectMapper.writeValueAsString(requestList);
            newFriendRequest.setUserId(toId);
            newFriendRequest.setRequestList(stringRequestList);
            friendRequestMapper.insert(newFriendRequest);
            return;
        }
           String requestList = friendRequest.getRequestList();
            Map<String, String> map = objectMapper.readValue(requestList, new TypeReference<Map<String, String>>() {});
            if(map.containsKey(String.valueOf(fromId))){
                throw new CustomException("Already sent request");
            }else {
                map.put(String.valueOf(fromId),msg);
                String newRequestList = objectMapper.writeValueAsString(map);
                friendRequest.setRequestList(newRequestList);
                friendRequestMapper.updateById(friendRequest);
            }
        } catch (IOException e) {
            throw new CustomException("System error");
        }
    }


    @Override
    @Transactional
    public void approveRequest(Long userId, Long approvedUserId) {
        deleteRequestInList(userId,approvedUserId);
        //add in both friend list
        addUserToFriendList(userId,approvedUserId);
        addUserToFriendList(approvedUserId,userId);

    }

    @Override
    public void addUserToFriendList(Long userId, Long approvedUserId) {
        System.out.println("add");
        User user = userMapper.selectById(userId);
        System.out.println(user);
        String friendList = user.getFriendList();

        log.info(friendList);
        try {
            Set<String> map = objectMapper.readValue(friendList==null?"{}":friendList, new TypeReference<Set<String>>() {});
            map.add(String.valueOf(approvedUserId));
            String newFriendList = objectMapper.writeValueAsString(map);
            user.setFriendList(newFriendList);
            userMapper.updateById(user);
/*            LambdaUpdateWrapper<User> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.set(User::getFriendList,newFriendList).eq(User::getId,user.getId());
            // 执行更新操作
            userMapper.update(null, updateWrapper);*/
        } catch (IOException e) {
            throw new CustomException("System error");
        }
    }

    @Override
    @Transactional
    public void rejectRequest(Long userId, Long approvedUserId) {
        deleteRequestInList(userId,approvedUserId);
    }



    @Override
    @Transactional
    public void deleteRequestInList(Long userId, Long rejectUserId) {
        MPJLambdaWrapper<FriendRequest> wrapper = new MPJLambdaWrapper<>();
        wrapper.eq(FriendRequest::getUserId, userId);
        FriendRequest friendRequest = friendRequestMapper.selectOne(wrapper);
        try {
            if(friendRequest==null){
                throw new CustomException("No Request found");
            }
            String requestList = friendRequest.getRequestList();
            Map<String, String> map = objectMapper.readValue(requestList, new TypeReference<Map<String, String>>() {});
            if(!map.containsKey(String.valueOf(rejectUserId))){
                throw new CustomException("No Request found");
            }else {
                map.remove(String.valueOf(rejectUserId));
                String newRequestList = objectMapper.writeValueAsString(map);
                friendRequest.setRequestList(newRequestList);
                friendRequestMapper.updateById(friendRequest);
            }
        } catch (IOException e) {
            throw new CustomException("System error");
        }
    }




    @Override
    @Transactional
    public List<UserDto> getAllRequest(Long userId) {
        List<UserDto> res = new ArrayList<>();
        MPJLambdaWrapper<FriendRequest> wrapper = new MPJLambdaWrapper<>();
        wrapper.eq(FriendRequest::getUserId, userId);
        FriendRequest friendRequest = friendRequestMapper.selectOne(wrapper);
        if(friendRequest==null){
           return res;
        }else {
            try {
                String requestList = friendRequest.getRequestList();
                Map<String, String> map = objectMapper.readValue(requestList, new TypeReference<Map<String, String>>() {});
                if(map.keySet().size()==0){
                    return res;
                }else {
                    MPJLambdaWrapper<User> wrapperTwo = new MPJLambdaWrapper<>();
                    wrapperTwo.selectAll(User.class).in(User::getId,convertToLongSet(map.keySet()));
                    res = userMapper.selectJoinList(UserDto.class, wrapperTwo);
                    res.forEach(userDto -> {
                        userDto.setMsg(map.get(String.valueOf(userDto.getId())));
                    });
                    return res;
                }
            }catch (IOException e) {
                throw new CustomException("System error");
            }

        }

    }


    public static Set<Long> convertToLongSet(Collection<String> stringCollection) {
        if (CollectionUtils.isEmpty(stringCollection)) {
            return null;
        }
        return stringCollection.stream()
                .filter(StringUtils::isNotBlank)
                .map(Long::valueOf)
                .collect(Collectors.toSet());
    }


}