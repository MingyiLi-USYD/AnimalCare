package usyd.mingyi.animalcare.pojo.chat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Message {
    private String userId;
    private long date;
    private String type;
    private String content;
}
