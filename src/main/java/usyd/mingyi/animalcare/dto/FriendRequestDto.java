package usyd.mingyi.animalcare.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import usyd.mingyi.animalcare.pojo.FriendRequest;
import usyd.mingyi.animalcare.pojo.User;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FriendRequestDto extends FriendRequest {
    User friendInfo;
}
