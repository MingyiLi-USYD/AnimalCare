package usyd.mingyi.animalcare.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.io.Serializable;
import java.util.List;

//用户信息
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User implements Serializable {

    private Long id;
    private String userName;
    private String password;
    private String email;
    @JsonProperty(value = "nickname")
    private String nickName;
    private String description;
    private String uuid;
    //@JsonProperty(value = "avatar",access = JsonProperty.Access.READ_ONLY)
    private String avatar;
    private String Tag;
    private List<Post> postList;
    private List<Pet> petList;
    private List<User> friendRecordList;


}
