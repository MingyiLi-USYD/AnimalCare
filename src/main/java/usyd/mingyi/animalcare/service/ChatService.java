package usyd.mingyi.animalcare.service;

import usyd.mingyi.animalcare.pojo.chat.Message;
import usyd.mingyi.animalcare.pojo.chat.RequestMessage;
import usyd.mingyi.animalcare.pojo.chat.ResponseMessage;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface ChatService {
    void sendMsgToFirebase(String currentId,String toId,  ResponseMessage requestMessage);
    CompletableFuture<List<Message>> retrieveDataFromFirebase(String fromId, String toId);
     void sendMsgToQueue(ResponseMessage responseMessage);
}
