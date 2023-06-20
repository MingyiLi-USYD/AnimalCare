package usyd.mingyi.animalcare.pojo;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Request {
    long fromId;
    long toId;
    String msg;
}
