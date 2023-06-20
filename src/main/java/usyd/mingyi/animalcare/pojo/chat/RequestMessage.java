package usyd.mingyi.animalcare.pojo.chat;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import usyd.mingyi.animalcare.pojo.User;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RequestMessage {
    private String toId;
    private Message message;
}
