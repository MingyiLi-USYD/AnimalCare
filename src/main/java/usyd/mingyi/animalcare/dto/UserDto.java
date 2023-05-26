package usyd.mingyi.animalcare.dto;

import usyd.mingyi.animalcare.pojo.Pet;
import usyd.mingyi.animalcare.pojo.Post;
import usyd.mingyi.animalcare.pojo.User;

import java.util.List;

public class UserDto extends User {
    private List<Post> postList;
    private List<Pet> petList;
    private List<User> friendRecordList;
}
