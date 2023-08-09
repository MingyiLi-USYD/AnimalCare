package usyd.mingyi.animalcare.socketEntity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import usyd.mingyi.animalcare.pojo.User;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ServiceMessage<T> extends RequestMessage {

    private ServiceMessageType  type; //0是删除好友//1是加好友 //2是同意加好友//3是拒绝加好友 //4是好友上线 //5是好友下线
    //6是收到新评论  //7是收到新的赞 //8是被其他人提及到 mention
    private T content;
    public ServiceMessage(String fromId, Long date, String toId,ServiceMessageType  type) {
        super(fromId, date, toId);
        this.type = type;
    }

}
