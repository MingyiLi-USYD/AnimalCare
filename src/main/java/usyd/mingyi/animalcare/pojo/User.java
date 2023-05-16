package usyd.mingyi.animalcare.pojo;

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

    private Integer id;
    private String userName;
    private String password;
    private String email;
    private String nickName;
    private String description;
    private String uuid;
    private String userImageAddress;
    private String Tag;
    private List<Post> postList;
    private List<Pet> petList;
    private List<User> friendRecordList;


}
