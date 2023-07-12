package usyd.mingyi.animalcare.socketEntity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RequestMessage {
    private String fromId;
    private Long date;
    private String toId;

}
