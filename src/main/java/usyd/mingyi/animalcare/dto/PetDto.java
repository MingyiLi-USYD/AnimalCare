package usyd.mingyi.animalcare.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import usyd.mingyi.animalcare.pojo.Pet;
import usyd.mingyi.animalcare.pojo.PetImage;

import java.util.List;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PetDto extends Pet {
    private List<PetImage> petImage;
}
