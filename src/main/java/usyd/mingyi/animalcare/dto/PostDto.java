package usyd.mingyi.animalcare.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import usyd.mingyi.animalcare.pojo.Post;
import usyd.mingyi.animalcare.pojo.PostImage;
import usyd.mingyi.animalcare.pojo.User;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostDto extends Post {
    @Valid
    @NotEmpty(message = "Must have images")
    private List<PostImage> images;
    @TableField(exist = false)
    private List<CommentDto> commentList;
    private User postUser;
    @TableField(exist = false)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Australia/Sydney") // 根据实际日期格式进行修改
    private Date estimateDate;
    @TableField(exist = false)
    private List<Long> referFriends;
}
