package usyd.mingyi.animalcare.pojo.chat;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import usyd.mingyi.animalcare.pojo.User;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ResponseMessage<T> {
    private boolean isSystem;
    private String type;
    private User fromUser;
    private String toUser;
    private T message;

}
