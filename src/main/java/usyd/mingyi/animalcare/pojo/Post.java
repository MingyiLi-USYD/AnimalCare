package usyd.mingyi.animalcare.pojo;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    private String postContent;
    private String postTag;
    private String PostTitle;
    private Long postTime; //sava timestamp
    private Long publishTime;
    private Boolean visible;
    private Long viewCount;
}
