package usyd.mingyi.animalcare.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import usyd.mingyi.animalcare.pojo.Comment;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentDto extends Comment {
    private String userAvatar;
    private String nickName;
    private String userName;
    private Long userId;

}
