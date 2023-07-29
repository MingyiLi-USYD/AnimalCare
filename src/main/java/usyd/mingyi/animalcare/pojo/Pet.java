package usyd.mingyi.animalcare.pojo;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Pet implements Serializable {
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @TableId
    private Long petId;
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long userId;
    private String petName;
    private Date birthday;
    private String category;
    private String petAvatar;
    private String petDescription;
    private Boolean petVisible;
}
