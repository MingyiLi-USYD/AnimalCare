package usyd.mingyi.animalcare.service.serviceImp;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import usyd.mingyi.animalcare.dto.FriendRequestDto;
import usyd.mingyi.animalcare.dto.FriendshipDto;
import usyd.mingyi.animalcare.mapper.FriendRequestMapper;
import usyd.mingyi.animalcare.mapper.FriendshipMapper;
import usyd.mingyi.animalcare.mapper.UserMapper;
import usyd.mingyi.animalcare.pojo.FriendRequest;
import usyd.mingyi.animalcare.pojo.Friendship;
import usyd.mingyi.animalcare.pojo.User;
import usyd.mingyi.animalcare.service.FriendshipService;

import java.util.List;

@Service
public class FriendshipServiceImp implements FriendshipService {

    @Autowired
    FriendshipMapper friendshipMapper;

    @Autowired
    UserMapper userMapper;

    @Autowired
    ObjectMapper objectMapper;

    @Override
    public List<FriendshipDto> getAllFriends(Long userId) {

        MPJLambdaWrapper<Friendship> query = new MPJLambdaWrapper<>();
        query.selectAll(Friendship.class)
                .selectAssociation(User.class, FriendshipDto::getFriendIno)
                .leftJoin(User.class,User::getUserId,Friendship::getFriendId)
                .eq(Friendship::getMyId,userId);
        List<FriendshipDto> friendshipDtos = friendshipMapper.selectJoinList(FriendshipDto.class, query);
        System.out.println(friendshipDtos);
        return friendshipDtos;

    }


    @Override
    @Transactional
    public int checkFriendshipStatus(long fromId, long toId) {

/*        try {
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
        return 3;*/
        return 0;
    }

    @Override
    @Transactional
    public void deleteUser(long fromId, long toId) {
        deleteUserInFriendList(fromId, toId);
        deleteUserInFriendList(toId, fromId);
    }

    @Override
    public User getFriendSync(long id) {
/*        MPJLambdaWrapper<User> wrapper = new MPJLambdaWrapper<>();
        wrapper.select(User::getNickname).select(User::getAvatar)
                .select(User::getId).select(User::getDescription).eq(User::getId,id);
        return userMapper.selectOne(wrapper);*/
        return null;
    }

    public void deleteUserInFriendList(Long userId, Long deleteUserId) {
/*        User user = userMapper.selectById(userId);
        String friendList = user.getFriendList();
        try {
            Set<String> map = objectMapper.readValue(friendList, new TypeReference<Set<String>>() {});
            map.remove(String.valueOf(deleteUserId));
            String newFriendList = objectMapper.writeValueAsString(map);
            user.setFriendList(newFriendList);
            userMapper.updateById(user);
        } catch (IOException e) {
            throw new CustomException("System error");
        }*/
    }
}
