package usyd.mingyi.animalcare.pojo.chat;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import usyd.mingyi.animalcare.pojo.User;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatInfo {
    String _id;
    Long date;
    @JsonProperty("user")
    User user;
    @JsonProperty("message")
    ChatMessage chatMessage;
}
