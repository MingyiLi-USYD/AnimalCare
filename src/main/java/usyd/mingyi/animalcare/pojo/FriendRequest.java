package usyd.mingyi.animalcare.pojo;


import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("request")
public class FriendRequest {
    @TableId
    Long requestId;
    Long userId;
    @TableField("request")
    String requestList;
}
