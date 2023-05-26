package usyd.mingyi.animalcare.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import usyd.mingyi.animalcare.mapper.PetMapper;
import usyd.mingyi.animalcare.pojo.Pet;

import java.util.List;

@Service
public class PetServiceImp implements PetService {

    @Autowired
    PetMapper petMapper;

    @Override
    public int addPet(Pet pet) {

        return petMapper.addPet(pet);
    }

    @Override
    public List<Pet> getPetList(long userId) {
        return petMapper.getPetList(userId);
    }

    @Override
    public Pet getPet(long petId, long useId) {
        return petMapper.getPet(petId, useId);
    }

    @Override
    public int deletePet(long petId, long useId) {
        return petMapper.deletePet(petId, useId);
    }

    @Override
    public int addImage(long imagePetId, String imageUrl) {
        return petMapper.addImage(imagePetId, imageUrl);
    }

}
