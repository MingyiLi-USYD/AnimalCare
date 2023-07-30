package usyd.mingyi.animalcare.service;

import com.baomidou.mybatisplus.extension.service.IService;
import usyd.mingyi.animalcare.pojo.Pet;
import usyd.mingyi.animalcare.pojo.PetImage;

import java.util.List;

public interface PetService extends IService<Pet> {
    List<Pet> getPetList(Long userId);
    Pet getPet(Long petId,Long useId);
    void deletePet(Long petId,Long useId);

    PetImage saveImageForPet(Long petId, Long userId, PetImage petImage);

    void  deleteImageForPet(Long petId,Long userId,Long imageId);


}
