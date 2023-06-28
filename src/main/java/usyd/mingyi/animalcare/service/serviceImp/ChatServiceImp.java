package usyd.mingyi.animalcare.service.serviceImp;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import usyd.mingyi.animalcare.pojo.chat.RequestMessage;
import usyd.mingyi.animalcare.pojo.chat.ResponseMessage;
import usyd.mingyi.animalcare.service.ChatService;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Service
public class ChatServiceImp implements ChatService {

   // private final DatabaseReference database;

    @Autowired
    public ChatServiceImp(FirebaseApp firebaseApp) {
       // this.database = FirebaseDatabase.getInstance(firebaseApp).getReference();
    }

    @Override
    public void sendMsgToFirebase(String currentId,String toId, ResponseMessage responseMessage) {

/*        DatabaseReference chatRef = database.child("users").child(String.valueOf(currentId)).child(toId);
        DatabaseReference newMessageRef = chatRef.child("messages").push();
        newMessageRef.setValue(responseMessage, null);

        DatabaseReference chatRef2 = database.child("users").child(toId).child(String.valueOf(currentId));
        DatabaseReference newMessageRef2 = chatRef2.child("messages").push();
        newMessageRef2.setValue(responseMessage, null);*/

    }

    // 生成聊天ID

}
