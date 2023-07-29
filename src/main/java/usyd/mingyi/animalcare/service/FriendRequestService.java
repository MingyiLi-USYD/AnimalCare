package usyd.mingyi.animalcare.service;

import com.baomidou.mybatisplus.extension.service.IService;
import usyd.mingyi.animalcare.dto.FriendRequestDto;
import usyd.mingyi.animalcare.dto.UserDto;
import usyd.mingyi.animalcare.pojo.FriendRequest;

import java.util.List;

public interface FriendRequestService extends IService<FriendRequest> {
    void sendRequest(Long fromId,Long toId,String msg);
    void deleteRequestInList(Long userId,Long rejectUserId);
    List<FriendRequestDto> getAllRequest(Long userId);

    UserDto getRequestById(Long userId,Long target);
    void approveRequest(Long userId,Long approvedUserId);
    void rejectRequest(Long userId,Long approvedUserId);
    void addUserToFriendList(Long userId,Long approvedUserId);

}
