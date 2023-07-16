package usyd.mingyi.animalcare.mongodb.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;
import usyd.mingyi.animalcare.pojo.User;
import usyd.mingyi.animalcare.socketEntity.ChatMessage;

import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "chat")
public class CloudMessage {
    @MongoId
    private String id;
    private Long latestTime;
    private List<String> participates;
    private List<ChatMessage> chatList;
    private User chatUser;
    private Long unRead;
}
