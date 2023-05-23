package usyd.mingyi.animalcare.pojo.chat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ResponseMessage {
    private boolean isSystem;
    private String type;
    private Long fromId;
    private Object message;

}
