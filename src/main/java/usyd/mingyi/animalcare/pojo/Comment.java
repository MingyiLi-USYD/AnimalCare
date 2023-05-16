package usyd.mingyi.animalcare.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Comment implements Serializable {
    private Integer id;
    private Integer postId;
    private Long commentTime;
    private String commentContent;
    private String userAvatar;
    private String nickName;
    private String userName;
    private int userId;

}
