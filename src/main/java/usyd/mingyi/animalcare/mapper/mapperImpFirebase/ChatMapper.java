package usyd.mingyi.animalcare.mapper.mapperImpFirebase;

import usyd.mingyi.animalcare.socketEntity.ChatMessage;
import usyd.mingyi.animalcare.socketEntity.ResponseMessage;

import java.util.List;
import java.util.concurrent.CompletableFuture;


public interface ChatMapper {
    void sendMsgToFirebase(String currentId,String toId,  ChatMessage chatMessage);
    CompletableFuture<List<ChatMessage>> retrieveDataFromFirebase(String fromId, String toId);
    CompletableFuture<Object> retrieveAllDataFromFirebase(String userId);
}
