package usyd.mingyi.animalcare.service;

import usyd.mingyi.animalcare.pojo.Pet;

import java.util.List;

public interface PetService {
    int addPet(Pet pet);
    List<Pet> getPetList(int userId);
    Pet getPet(int petId,int useId);
    int deletePet(int petId,int useId);
    int addImage(int imagePetId,String imageUrl);
}
