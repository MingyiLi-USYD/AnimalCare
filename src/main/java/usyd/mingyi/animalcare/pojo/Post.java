package usyd.mingyi.animalcare.pojo;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("post")
public class Post implements Serializable {
    @TableId("post_id")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long postId;
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long userId;
    private Long love;
    @NotBlank(message = "Must have content")
    private String postContent;
    @NotBlank(message = "Must define tag")
    private String postTag;
    @NotBlank(message = "Must have title")
    private String postTitle;
    private Long postTime; //sava timestamp
    private Long publishTime;
    @NotNull(message = "Must define visibility")
    private Boolean visible;
    private Long viewCount;
    private String coverImage;
}
