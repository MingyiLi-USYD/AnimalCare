package usyd.mingyi.animalcare.pojo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("post")
public class Post implements Serializable {
    @TableId("post_id")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long postId;
    @TableField("post_user_id")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long userId;
    private long love;
    @NotBlank
    private String postContent;
    private String tag;
    @NotBlank
    private String topic;
    private Long postTime; //sava timestamp
    @NotBlank
    private String images;
    @NotBlank
    private boolean visible;


}
