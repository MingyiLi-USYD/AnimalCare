package usyd.mingyi.animalcare.pojo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Pet implements Serializable {
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @TableId
    private Long petId;
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @TableField("pet_user_id")
    private Long userId;
    @TableField("name")
    private String petName;
    private Integer age;
    private String category;
    @TableField("pet_image_address")
    private String petAvatar;
    private String petDescription;
    private boolean petVisible;
    @TableField(exist = false)
    private List<Image> petImageList;

}
