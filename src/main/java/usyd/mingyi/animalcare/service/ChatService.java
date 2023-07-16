package usyd.mingyi.animalcare.service;

import usyd.mingyi.animalcare.mongodb.entity.CloudMessage;
import usyd.mingyi.animalcare.socketEntity.ChatMessage;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface ChatService {
    CloudMessage retrieveDataFromMongoDB(String fromId, String toId);
    CloudMessage retrieveDataFromMongoDB(Long fromId, Long toId);
    List<CloudMessage>  retrieveAllDataFromMongoDB(String userId);
     void sendMsgToQueue(ChatMessage chatMessage);

     void saveMsgInCloud(ChatMessage chatMessage);
}
