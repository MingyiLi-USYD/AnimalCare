package usyd.mingyi.animalcare.pojo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("subcomment")
public class Subcomment {
    @TableId("subcomment_id")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long subcommentId;
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @TableField("subcomment_comment_id")
    private Long commentId;
    @TableField("subcomment_user_id")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long userId;
    private Long subcommentTime;
    private String subcommentContent;
    private Long subcommentLove;
    @TableField(exist = false)
    private String targetNickname;
}
