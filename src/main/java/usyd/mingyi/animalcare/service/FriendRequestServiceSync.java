package usyd.mingyi.animalcare.service;

import usyd.mingyi.animalcare.dto.UserDto;

import java.util.List;

public interface FriendRequestServiceSync {
    void sendRequestSync(Long fromId,Long toId,String msg);
    void approveRequestSync(Long userId,Long approvedUserId);
    void rejectRequestSync(Long userId,Long approvedUserId);
}
