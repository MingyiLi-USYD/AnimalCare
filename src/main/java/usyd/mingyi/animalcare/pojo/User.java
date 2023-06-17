package usyd.mingyi.animalcare.pojo;
import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;

//用户信息
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User implements Serializable {
    private static final long serialVersionUID = 1L;
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long id;
    @TableField("username")
    private String userName;
    private String password;
    private String email;
    @JsonProperty(value = "nickname")
    @TableField("nickname")
    private String nickName;
    private String description;
    private String uuid;
    private String avatar;
    private String Tag;
    private String loveList;
}
