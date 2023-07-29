package usyd.mingyi.animalcare.service;

import com.baomidou.mybatisplus.extension.service.IService;
import usyd.mingyi.animalcare.pojo.Pet;

import java.util.List;

public interface PetService extends IService<Pet> {
    List<Pet> getPetList(Long userId);
    Pet getPet(Long petId,Long useId);
    int deletePet(Long petId,Long useId);

}
