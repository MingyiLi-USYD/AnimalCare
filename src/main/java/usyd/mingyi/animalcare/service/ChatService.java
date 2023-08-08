package usyd.mingyi.animalcare.service;

import usyd.mingyi.animalcare.mongodb.entity.CloudMessage;
import usyd.mingyi.animalcare.socketEntity.ChatMessage;

import java.util.List;
import java.util.Map;

public interface ChatService {
    CloudMessage retrieveDataFromMongoDB(String fromId, String toId);
    CloudMessage retrieveDataFromMongoDB(Long fromId, Long toId);
    List<CloudMessage>  retrieveAllDataFromMongoDB(String userId);
    List<CloudMessage>  retrievePartlyDataFromMongoDB(String userId, Map<String,Long> localStorage);
     void sendMsgToQueue(ChatMessage chatMessage);

     void saveMsgInCloud(ChatMessage chatMessage);
}
