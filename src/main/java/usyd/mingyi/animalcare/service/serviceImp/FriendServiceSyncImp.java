package usyd.mingyi.animalcare.service.serviceImp;

import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import usyd.mingyi.animalcare.service.FriendServiceSync;
import usyd.mingyi.animalcare.service.FriendshipService;
import usyd.mingyi.animalcare.service.RealTimeService;
import usyd.mingyi.animalcare.socketEntity.ServiceMessage;

@Service
public class FriendServiceSyncImp implements FriendServiceSync {
    @Autowired
    FriendshipService friendService;
    @Autowired
    RealTimeService realTimeService;
    @Override
    public void deleteUserSync(long fromId, long toId) {
        friendService.deleteUser(fromId,toId);
        realTimeService.remindFriends(new ServiceMessage(String.valueOf(fromId),System.currentTimeMillis(),String.valueOf(toId),0));
    }
}
