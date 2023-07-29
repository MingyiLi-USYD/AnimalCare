package usyd.mingyi.animalcare.service.serviceImp;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import usyd.mingyi.animalcare.service.FriendRequestService;
import usyd.mingyi.animalcare.service.FriendRequestServiceSync;

import usyd.mingyi.animalcare.service.RealTimeService;
import usyd.mingyi.animalcare.socketEntity.ServiceMessage;

@Slf4j
@Service
public class FriendRequestServiceSyncImp implements FriendRequestServiceSync {

    @Autowired
    FriendRequestService friendRequestService;
    @Autowired
    RealTimeService realTimeService;
    @Override
    public void sendRequestSync(Long fromId, Long toId, String msg) {
        friendRequestService.sendRequest(fromId,toId,msg);
        realTimeService.remindFriends(new ServiceMessage(String.valueOf(fromId),System.currentTimeMillis(),String.valueOf(toId),1));
    }

    @Override
    public void approveRequestSync(Long userId, Long approvedUserId) {
        friendRequestService.approveRequest(userId,approvedUserId);
        realTimeService.remindFriends(new ServiceMessage(String.valueOf(userId),System.currentTimeMillis(),String.valueOf(approvedUserId),2));
    }

    @Override
    public void rejectRequestSync(Long userId, Long approvedUserId) {
        friendRequestService.rejectRequest(userId,approvedUserId);
        realTimeService.remindFriends(new ServiceMessage(String.valueOf(userId),System.currentTimeMillis(),String.valueOf(approvedUserId),3));
    }


}
