package usyd.mingyi.animalcare.socketEntity;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SystemMessage extends RequestMessage{
     public SystemMessage(String fromId, Long date, String toId, String fromServer) {
          super(fromId, date, toId);
          this.fromServer = fromServer;
     }

     String fromServer;
}
