package usyd.mingyi.animalcare.service;

import org.springframework.amqp.core.Message;
import usyd.mingyi.animalcare.socketEntity.ChatMessage;
import usyd.mingyi.animalcare.socketEntity.ResponseMessage;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface ChatService {
    void sendMsgToFirebase(String currentId,String toId,  ChatMessage chatMessage);
    CompletableFuture<List<ChatMessage>> retrieveDataFromFirebase(String fromId, String toId);
     void sendMsgToQueue(ChatMessage chatMessage);
}
