package usyd.mingyi.animalcare.pojo;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("mention")
public class Mention implements Serializable {
    @TableId
    private Long mentionId;
    private Long postId;
    private Long userId;
    private Boolean isRead;


    public Mention(Long postId, Long userId) {
        this.postId = postId;
        this.userId = userId;
    }
}
