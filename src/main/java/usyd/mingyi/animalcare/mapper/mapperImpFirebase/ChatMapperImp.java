package usyd.mingyi.animalcare.mapper.mapperImpFirebase;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import usyd.mingyi.animalcare.mapper.ChatMapper;
import usyd.mingyi.animalcare.socketEntity.ChatMessage;
import usyd.mingyi.animalcare.socketEntity.ResponseMessage;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Component
public class ChatMapperImp implements ChatMapper {

    private final DatabaseReference database;

    @Autowired
    public ChatMapperImp(FirebaseApp firebaseApp) {
        this.database = FirebaseDatabase.getInstance(firebaseApp).getReference();
    }

    @Override
    public void sendMsgToFirebase(String currentId,String toId, ChatMessage chatMessage) {

        DatabaseReference chatRef = database.child("users").child(String.valueOf(currentId)).child(toId);
        DatabaseReference newMessageRef = chatRef.child("messages").push();
        newMessageRef.setValue(chatMessage, null);

        DatabaseReference chatRef2 = database.child("users").child(toId).child(String.valueOf(currentId));
        DatabaseReference newMessageRef2 = chatRef2.child("messages").push();
        newMessageRef2.setValue(chatMessage, null);
    }


    public CompletableFuture<List<ChatMessage>> retrieveDataFromFirebase(String fromId, String toId) {
        CompletableFuture<List<ChatMessage>> future = new CompletableFuture<>();

        Query query = database.child("users")
                .child(fromId).child(toId).child("messages")
                .orderByChild("message/date");

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<ChatMessage> messageList = new ArrayList<>();

                for (DataSnapshot messageSnapshot : dataSnapshot.getChildren()) {
                    ChatMessage chatMessage = messageSnapshot.getValue(ChatMessage.class);
                    messageList.add(chatMessage);
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
}
