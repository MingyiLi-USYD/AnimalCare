package usyd.mingyi.animalcare.service;

import com.baomidou.mybatisplus.extension.service.IService;
import usyd.mingyi.animalcare.pojo.Pet;

import java.util.List;

public interface PetService extends IService<Pet> {
    List<Pet> getPetList(long userId);
    Pet getPet(long petId,long useId);
    int deletePet(long petId,long useId);

}
