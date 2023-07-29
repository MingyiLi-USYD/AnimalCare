package usyd.mingyi.animalcare.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import usyd.mingyi.animalcare.pojo.Pet;
import usyd.mingyi.animalcare.pojo.Post;
import usyd.mingyi.animalcare.pojo.User;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDto extends User {
    private List<Post> postList;
    private List<Pet> petList;
    private List<Post> loveList;
    private List<FriendshipDto> friendList;
    private List<RequestUserDto> requestUserDtoList;


}
