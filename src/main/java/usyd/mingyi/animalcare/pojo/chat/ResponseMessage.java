package usyd.mingyi.animalcare.pojo.chat;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ResponseMessage {
    private boolean isSystem;
    private String type;
    //@JsonFormat(shape = JsonFormat.Shape.STRING)
    private String fromId;
    private Object message;

}
