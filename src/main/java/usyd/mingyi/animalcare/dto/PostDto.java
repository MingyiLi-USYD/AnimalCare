package usyd.mingyi.animalcare.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import usyd.mingyi.animalcare.pojo.Comment;
import usyd.mingyi.animalcare.pojo.Post;
import usyd.mingyi.animalcare.pojo.PostImage;
import usyd.mingyi.animalcare.pojo.User;

import javax.validation.constraints.NotBlank;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostDto extends Post {

    private List<PostImage> images;
    @TableField(exist = false)
    private List<Comment> commentList;
    private User postUser;
}
