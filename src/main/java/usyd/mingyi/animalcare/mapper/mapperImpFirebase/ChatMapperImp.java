package usyd.mingyi.animalcare.mapper.mapperImpFirebase;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import usyd.mingyi.animalcare.pojo.CloudMessage;
import usyd.mingyi.animalcare.socketEntity.ChatMessage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Component
public class ChatMapperImp implements ChatMapper {

    private final DatabaseReference database;

    @Autowired
    public ChatMapperImp(FirebaseApp firebaseApp) {
        this.database = FirebaseDatabase.getInstance(firebaseApp).getReference();
    }

    @Override
    public void sendMsgToFirebase(String currentId,String toId, ChatMessage chatMessage) {
        String[] ids = {currentId, toId};
        Arrays.sort(ids);
        String combinedId = String.join("_", ids);

        DatabaseReference chatRef = database.child("chat").child(combinedId);
        // 获取当前的 CloudMessage 对象
        chatRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    CloudMessage cloudMessage = dataSnapshot.getValue(CloudMessage.class);
                    // 更新 lastTime 字段为当前时间
                    cloudMessage.setLastTime(System.currentTimeMillis());

                    // 将 ChatMessage 添加到 chatHistory 列表的末尾
                    cloudMessage.getChatHistory().add(chatMessage);

                    // 更新 CloudMessage 对象到 Firebase 数据库
                    chatRef.setValue(cloudMessage,null);
                }else {

                    List<ChatMessage> chatMessages = new ArrayList<>(1);
                    chatMessages.add(chatMessage);
                    CloudMessage cloudMessage = new CloudMessage();
                    cloudMessage.setLastTime(System.currentTimeMillis());
                    cloudMessage.setChatHistory(chatMessages);
                    cloudMessage.setParticipates(Arrays.stream(ids).collect(Collectors.toList()));
                    chatRef.setValue(cloudMessage,null);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // 处理取消事件
            }
        });
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

    @Override
    public CompletableFuture<Object> retrieveAllDataFromFirebase(String userId) {

        return null;
    }
}
