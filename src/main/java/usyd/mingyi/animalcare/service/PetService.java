package usyd.mingyi.animalcare.service;

import usyd.mingyi.animalcare.pojo.Pet;

import java.util.List;

public interface PetService {
    int addPet(Pet pet);
    List<Pet> getPetList(long userId);
    Pet getPet(long petId,long useId);
    int deletePet(long petId,long useId);
    int addImage(long imagePetId,String imageUrl);
}
