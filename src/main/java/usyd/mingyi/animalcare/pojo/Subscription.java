package usyd.mingyi.animalcare.pojo;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("subscription")
public class Subscription implements Serializable {
    @TableId
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    Long subscriptionId;
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    Long myId;
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    Long subscribedUserId;
}
