package usyd.mingyi.animalcare.service.serviceImp;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.core.Message;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.*;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import usyd.mingyi.animalcare.common.CustomException;
import usyd.mingyi.animalcare.component.ClientCache;
import usyd.mingyi.animalcare.config.MQConfig;
import usyd.mingyi.animalcare.pojo.chat.ResponseMessage;
import usyd.mingyi.animalcare.service.ChatService;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class ChatServiceImp implements ChatService {
    @Resource
    private ClientCache clientCache;
    @Resource
    private ObjectMapper objectMapper;

    private final DatabaseReference database;

    private final RabbitTemplate rabbitTemplate;


    @Autowired
    public ChatServiceImp(FirebaseApp firebaseApp,RabbitTemplate rabbitTemplate) {
        this.database = FirebaseDatabase.getInstance(firebaseApp).getReference();
        this.rabbitTemplate = rabbitTemplate;
        //rabbitTemplate.setConfirmCallback(confirmCallback());
    }

    @Override
    public void sendMsgToFirebase(String currentId,String toId, ResponseMessage responseMessage) {

        DatabaseReference chatRef = database.child("users").child(String.valueOf(currentId)).child(toId);
        DatabaseReference newMessageRef = chatRef.child("messages").push();
        newMessageRef.setValue(responseMessage.getMessage(), null);

        DatabaseReference chatRef2 = database.child("users").child(toId).child(String.valueOf(currentId));
        DatabaseReference newMessageRef2 = chatRef2.child("messages").push();
        newMessageRef2.setValue(responseMessage.getMessage(), null);
    }


    public CompletableFuture<List<usyd.mingyi.animalcare.pojo.chat.Message>> retrieveDataFromFirebase(String fromId, String toId) {
        CompletableFuture<List<usyd.mingyi.animalcare.pojo.chat.Message>> future = new CompletableFuture<>();

        Query query = database.child("users")
                .child(fromId).child(toId).child("messages")
                .orderByChild("message/date");

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<usyd.mingyi.animalcare.pojo.chat.Message> messageList = new ArrayList<>();

                for (DataSnapshot messageSnapshot : dataSnapshot.getChildren()) {
                    usyd.mingyi.animalcare.pojo.chat.Message responseMessage = messageSnapshot.getValue(usyd.mingyi.animalcare.pojo.chat.Message.class);
                    messageList.add(responseMessage);
                }

                future.complete(messageList);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // 处理取消事件
                future.completeExceptionally(databaseError.toException());
            }
        });

        return future;
    }
    // 生成聊天ID

    public void sendMsgToQueue(ResponseMessage responseMessage){
        try {
            System.out.println("正在发送");
            rabbitTemplate.convertAndSend(MQConfig.MESSAGE_EXCHANGE,"#",objectMapper.writeValueAsString(responseMessage));
        } catch (JsonProcessingException e) {
            throw new CustomException("System Error");
        }
    }

/*    private RabbitTemplate.ConfirmCallback confirmCallback() {
        return (correlationData, ack, cause) -> {
            if (ack) {
                System.out.println("Message successfully published.");
            } else {
                System.out.println("Failed to publish message: " + cause);
                requeueMessage(correlationData.getReturnedMessage());
            }
        };
    }*/

    private void requeueMessage(Message returnedMessage) {
        rabbitTemplate.send(returnedMessage.getMessageProperties().getReceivedExchange(),
                returnedMessage.getMessageProperties().getReceivedRoutingKey(),
                returnedMessage);
    }
}
