package usyd.mingyi.animalcare.mapper.mapperImpFirebase;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.*;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import usyd.mingyi.animalcare.mapper.ChatMapper;
import usyd.mingyi.animalcare.pojo.chat.ResponseMessage;

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
}
