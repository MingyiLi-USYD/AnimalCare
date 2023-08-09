package usyd.mingyi.animalcare.service;

import com.baomidou.mybatisplus.extension.service.IService;
import usyd.mingyi.animalcare.dto.FriendRequestDto;
import usyd.mingyi.animalcare.dto.FriendshipDto;
import usyd.mingyi.animalcare.dto.UserDto;
import usyd.mingyi.animalcare.pojo.FriendRequest;

import java.util.List;

public interface FriendRequestService extends IService<FriendRequest> {
    void sendRequest(Long fromId,Long toId,String msg);

    List<FriendRequestDto> getAllRequests(Long userId);
    List<FriendRequestDto> getAllRequestsAndMarkRead(Long userId);

    void approveRequest(Long userId,Long approvedUserId);
    void rejectRequest(Long userId,Long approvedUserId);
    void addUserToFriendList(Long userId,Long approvedUserId);

    void sendRequestSyncSocket(Long fromId, Long toId, String msg);
    FriendshipDto approveRequestAndGetSyncSocket(Long userId, Long approvedUserId);
    void rejectRequestSyncSocket(Long userId, Long targetUserId);

}
