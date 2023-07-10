package usyd.mingyi.animalcare.service.serviceImp;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import usyd.mingyi.animalcare.common.CustomException;
import usyd.mingyi.animalcare.mapper.FriendRequestMapper;
import usyd.mingyi.animalcare.mapper.UserMapper;
import usyd.mingyi.animalcare.pojo.FriendRequest;
import usyd.mingyi.animalcare.pojo.User;
import usyd.mingyi.animalcare.service.FriendService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class FriendServiceImp implements FriendService {

    @Autowired
    FriendRequestMapper friendRequestMapper;

    @Autowired
    UserMapper userMapper;

    @Autowired
    ObjectMapper objectMapper;

    @Override
    public List<User> getAllFriends(long id) {
        List<User> res = new ArrayList<>();
        User user = userMapper.selectById(id);
        String friendList = user.getFriendList();
        System.out.println(friendList);
        if(friendList==null){
            return res;
        }
        try {
            Set<String> set = objectMapper.readValue(friendList, new TypeReference<Set<String>>() {});
            if(set.size()==0){
                return res;
            }else {
                MPJLambdaWrapper<User> wrapperTwo = new MPJLambdaWrapper<>();
                wrapperTwo.select(User::getNickname).select(User::getAvatar)
                        .select(User::getId).select(User::getDescription)
                        .in(User::getId,FriendRequestServiceImp.convertToLongSet(set));
                return userMapper.selectList(wrapperTwo);
            }
        }catch (IOException e) {
            throw new CustomException("System error");
        }

    }



    @Override
    @Transactional
    public int checkFriendshipStatus(long fromId, long toId) {

        try {
            User user = userMapper.selectById(toId);
            String friendList = user.getFriendList();
            if(friendList!=null){
                Set<String> map = objectMapper.readValue(friendList, new TypeReference<Set<String>>() {});
                if(map.contains(String.valueOf(fromId))){
                    return 1;
                }
            }
            MPJLambdaWrapper<FriendRequest> wrapper = new MPJLambdaWrapper<>();
            wrapper.eq(FriendRequest::getUserId, toId);
            FriendRequest friendRequest = friendRequestMapper.selectOne(wrapper);
            if(friendRequest==null){
                return 3;
            }

            String requestList = friendRequest.getRequestList();
            Map<String, String> mapTwo = objectMapper.readValue(requestList, new TypeReference<Map<String, String>>() {
            });
            if (mapTwo.containsKey(String.valueOf(fromId))) {
                return 2;
            }
        }catch (IOException e) {
            throw new CustomException("System error");
        }
        return 3;

    }

    @Override
    @Transactional
    public void deleteUser(long fromId, long toId) {
        deleteUserInFriendList(fromId,toId);
        deleteUserInFriendList(toId,fromId);
    }

    @Override
    public User getFriendSync(long id) {
        MPJLambdaWrapper<User> wrapper = new MPJLambdaWrapper<>();
        wrapper.select(User::getNickname).select(User::getAvatar)
                .select(User::getId).select(User::getDescription).eq(User::getId,id);
        return userMapper.selectOne(wrapper);
    }

    public void deleteUserInFriendList(Long userId, Long deleteUserId) {
        User user = userMapper.selectById(userId);
        String friendList = user.getFriendList();
        try {
            Set<String> map = objectMapper.readValue(friendList, new TypeReference<Set<String>>() {});
            map.remove(String.valueOf(deleteUserId));
            String newFriendList = objectMapper.writeValueAsString(map);
            user.setFriendList(newFriendList);
            userMapper.updateById(user);
        } catch (IOException e) {
            throw new CustomException("System error");
        }
    }
}
