package usyd.mingyi.animalcare.pojo.chat;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RequestMessage {
    //@JsonFormat(shape = JsonFormat.Shape.STRING)
    private String toId;
    private ChatInfo message;

}
