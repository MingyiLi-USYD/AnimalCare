package usyd.mingyi.animalcare.service.serviceImp;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import usyd.mingyi.animalcare.dto.FriendshipDto;
import usyd.mingyi.animalcare.mapper.FriendshipMapper;
import usyd.mingyi.animalcare.pojo.Friendship;
import usyd.mingyi.animalcare.pojo.User;
import usyd.mingyi.animalcare.service.FriendRequestService;
import usyd.mingyi.animalcare.service.FriendRequestServiceSync;

import usyd.mingyi.animalcare.service.FriendshipService;
import usyd.mingyi.animalcare.service.RealTimeService;
import usyd.mingyi.animalcare.socketEntity.ServiceMessage;

@Slf4j
@Service
public class FriendRequestServiceSyncImp implements FriendRequestServiceSync {

    @Autowired
    FriendRequestService friendRequestService;

    @Autowired
    FriendshipService friendshipService;

    @Autowired
    RealTimeService realTimeService;

    @Override
    public void sendRequestSync(Long fromId, Long toId, String msg) {
        friendRequestService.sendRequest(fromId,toId,msg);
        //realTimeService.remindFriends(new ServiceMessage(String.valueOf(fromId),System.currentTimeMillis(),String.valueOf(toId),1));
    }

    @Override
    public FriendshipDto approveRequestSync(Long userId, Long approvedUserId) {
        friendRequestService.approveRequest(userId,approvedUserId);
        //同时同步通知此人如果在线的话
       // realTimeService.remindFriends(new ServiceMessage(String.valueOf(userId),System.currentTimeMillis(),String.valueOf(approvedUserId),2));
        return friendshipService.getFriendshipByIds(userId,approvedUserId);
    }

    @Override
    public void rejectRequestSync(Long userId, Long approvedUserId) {
        friendRequestService.rejectRequest(userId,approvedUserId);
        //realTimeService.remindFriends(new ServiceMessage(String.valueOf(userId),System.currentTimeMillis(),String.valueOf(approvedUserId),3));
    }


}
