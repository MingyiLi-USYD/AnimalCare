package usyd.mingyi.animalcare.pojo;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("love_post")
public class LovePost implements Serializable {
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @TableId
    private Long loveId;
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long userId;
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long postId;
    private Boolean isRead;
    private Boolean isCanceled;
}
