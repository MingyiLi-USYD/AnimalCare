package usyd.mingyi.animalcare.pojo;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@TableName("pet_image")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PetImage {
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    Long imageId;
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    Long petId;
    String imageUrl;
}
