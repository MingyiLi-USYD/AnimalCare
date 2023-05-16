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
    public List<Pet> getPetList(int userId) {
        return petMapper.getPetList(userId);
    }

    @Override
    public Pet getPet(int petId, int useId) {
        return petMapper.getPet(petId, useId);
    }

    @Override
    public int deletePet(int petId, int useId) {
        return petMapper.deletePet(petId, useId);
    }

    @Override
    public int addImage(int imagePetId, String imageUrl) {
        return petMapper.addImage(imagePetId, imageUrl);
    }

}
