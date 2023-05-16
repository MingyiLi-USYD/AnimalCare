package usyd.mingyi.animalcare.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Pet implements Serializable {
    private Integer petId;
    private Integer userId;
    private String petName;
    private Integer age;
    private String category;
    private String petImageAddress;
    private String petDescription;
    private List<String> petImageList;

}
