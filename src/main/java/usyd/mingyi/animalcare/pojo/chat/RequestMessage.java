package usyd.mingyi.animalcare.pojo.chat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RequestMessage {
    private Integer toId;
    private ChatInfo message;

}
