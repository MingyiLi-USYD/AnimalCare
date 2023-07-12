package usyd.mingyi.animalcare.socketEntity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import usyd.mingyi.animalcare.pojo.User;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessage extends RequestMessage{
    private String content;

    public ChatMessage(String fromId, Long date, String toId, String content) {
        super(fromId, date, toId);
        this.content = content;
    }
}
