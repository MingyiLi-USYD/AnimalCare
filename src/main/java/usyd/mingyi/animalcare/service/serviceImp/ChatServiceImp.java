package usyd.mingyi.animalcare.service.serviceImp;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import usyd.mingyi.animalcare.pojo.chat.Message;
import usyd.mingyi.animalcare.pojo.chat.RequestMessage;
import usyd.mingyi.animalcare.pojo.chat.ResponseMessage;
import usyd.mingyi.animalcare.service.ChatService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class ChatServiceImp implements ChatService {

    private final DatabaseReference database;

    @Autowired
    public ChatServiceImp(FirebaseApp firebaseApp) {
        this.database = FirebaseDatabase.getInstance(firebaseApp).getReference();
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


    public CompletableFuture<List<Message>> retrieveDataFromFirebase(String fromId, String toId) {
        CompletableFuture<List<Message>> future = new CompletableFuture<>();

        Query query = database.child("users")
                .child(fromId).child(toId).child("messages")
                .orderByChild("message/date");

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Message> messageList = new ArrayList<>();

                for (DataSnapshot messageSnapshot : dataSnapshot.getChildren()) {
                    Message responseMessage = messageSnapshot.getValue(Message.class);
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

}
