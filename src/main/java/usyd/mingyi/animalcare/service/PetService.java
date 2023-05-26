package usyd.mingyi.animalcare.service;

import usyd.mingyi.animalcare.pojo.Pet;

import java.util.List;

public interface PetService {
    int addPet(Pet pet);
    List<Pet> getPetList(long userId);
    Pet getPet(int petId,long useId);
    int deletePet(int petId,long useId);
    int addImage(int imagePetId,String imageUrl);
}
