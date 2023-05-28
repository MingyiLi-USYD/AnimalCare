package usyd.mingyi.animalcare.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import usyd.mingyi.animalcare.pojo.Comment;
import usyd.mingyi.animalcare.pojo.Post;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostDto extends Post {

    @TableField(exist = false)
    private List<Comment> commentList;
    @TableField(exist = false)
    private List<String> videoUrlList;
    @TableField(value = "avatar" )
    private String userAvatar;
    @TableField(exist = false)
    private boolean loved;
    private String nickName;
    @TableField(exist = false)
    private Integer visitCount;
}
