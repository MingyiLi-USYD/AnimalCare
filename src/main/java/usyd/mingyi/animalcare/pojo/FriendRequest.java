package usyd.mingyi.animalcare.pojo;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("request")
public class FriendRequest {
    @TableId
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    Long requestId;
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    Long myId;
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    Long friendId;
    String msg;
    Boolean isRead;
}
