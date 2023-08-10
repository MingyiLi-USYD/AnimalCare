package usyd.mingyi.animalcare.pojo;

import com.baomidou.mybatisplus.annotation.TableField;
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
@TableName("subcomment")
public class Subcomment implements Serializable {
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @TableId
    private Long subcommentId;
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long commentId;
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long userId;
    private Long subcommentTime;
    private String subcommentContent;
    private Long subcommentLove;
    private String targetNickname;
}
