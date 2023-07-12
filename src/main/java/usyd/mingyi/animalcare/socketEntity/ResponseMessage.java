package usyd.mingyi.animalcare.socketEntity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import usyd.mingyi.animalcare.pojo.User;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ResponseMessage<T> {
    private Integer code; // 0是连接错误 //1是Chat //2是Service //3是系统
    private T message;
    private User fromUser;
}
