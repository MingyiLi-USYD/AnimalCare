package usyd.mingyi.animalcare.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Comment implements Serializable {
    private Long id;
    private Long postId;
    private Long commentTime;
    private String commentContent;
    private String userAvatar;
    private String nickName;
    private String userName;
    private Long userId;

}
