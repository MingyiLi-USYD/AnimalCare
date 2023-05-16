package usyd.mingyi.animalcare.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Post implements Serializable {
    private Integer postId;
    private Integer userId;
    private Integer love;
    private String postContent;
    private String tag;
    private String topic;
    private Long posTime; //sava timestamp
    private List<Comment> commentList;
    private List<String> videoUrlList;
    private List<String> imageUrlList;
    private String userAvatar;
    private boolean loved;
    private Integer totalPosts;
    private String nickName;
    private String userName;
    private Integer visitCount;


}
