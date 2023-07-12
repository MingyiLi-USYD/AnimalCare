package usyd.mingyi.animalcare.socketEntity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import usyd.mingyi.animalcare.pojo.User;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ServiceMessage extends RequestMessage {
    private Integer type; //0是删除好友//1是加好友 //2是拒绝加好友 //3是好友上线 //4是好友下线
    public ServiceMessage(String fromId, Long date, String toId, Integer type) {
        super(fromId, date, toId);
        this.type = type;
    }
}
