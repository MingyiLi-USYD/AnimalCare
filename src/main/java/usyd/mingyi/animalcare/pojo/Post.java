package usyd.mingyi.animalcare.pojo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("post")
public class Post implements Serializable {
    @TableId("post_id")
    private Long postId;
    @TableField("post_user_id")
    private Long userId;
    private Integer love;
    private String postContent;
    private String tag;
    private String topic;
    private Long postTime; //sava timestamp
    @TableField(exist = false)
    private List<Comment> commentList;
    @TableField(exist = false)
    private List<String> videoUrlList;
    @TableField(exist = false)
    private List<String> imageUrlList;
    @TableField(exist = false)
    private String userAvatar;
    @TableField(exist = false)
    private boolean loved;
    @TableField(exist = false)
    private Integer totalPosts;
    @TableField(exist = false)
    private String nickName;
    @TableField(exist = false)
    private String userName;
    @TableField(exist = false)
    private Integer visitCount;


}
