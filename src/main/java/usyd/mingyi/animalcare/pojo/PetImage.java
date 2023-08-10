package usyd.mingyi.animalcare.pojo;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

@TableName("pet_image")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PetImage implements Serializable {
    @TableId
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    Long imageId;
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    Long petId;
    @NotBlank(message = "Pet imageUrl can not be blank")
    String imageUrl;
    @NotBlank(message = "Pet fileName can not be blank")
    String fileName;
}
