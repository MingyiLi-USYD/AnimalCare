package usyd.mingyi.animalcare.mapper;

import org.apache.ibatis.annotations.Mapper;
import usyd.mingyi.animalcare.pojo.chat.Message;
import usyd.mingyi.animalcare.pojo.chat.ResponseMessage;

import java.util.List;
import java.util.concurrent.CompletableFuture;


public interface ChatMapper {
    void sendMsgToFirebase(String currentId,String toId,  ResponseMessage requestMessage);
    CompletableFuture<List<Message>> retrieveDataFromFirebase(String fromId, String toId);
}
