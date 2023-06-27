package usyd.mingyi.animalcare.service;

import usyd.mingyi.animalcare.pojo.chat.RequestMessage;
import usyd.mingyi.animalcare.pojo.chat.ResponseMessage;

public interface ChatService {
    void sendMsgToFirebase(String currentId,String toId,  ResponseMessage requestMessage);
}
