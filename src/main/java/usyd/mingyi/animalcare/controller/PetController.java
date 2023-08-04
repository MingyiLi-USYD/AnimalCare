package usyd.mingyi.animalcare.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import usyd.mingyi.animalcare.common.R;
import usyd.mingyi.animalcare.pojo.Pet;
import usyd.mingyi.animalcare.pojo.PetImage;
import usyd.mingyi.animalcare.service.PetService;
import usyd.mingyi.animalcare.utils.BaseContext;

import java.util.List;

@RestController
public class PetController {

    @Autowired
    PetService petService;

    @PostMapping("/pet")
    public R<String> addPet(@RequestBody Pet pet) {
        //拿到自己的id
        Long currentId = BaseContext.getCurrentId();
        pet.setUserId(currentId);
        //保存宠物
        petService.save(pet);
        return R.success("成功了");
    }

    @GetMapping("/pets")
    public R<List<Pet>> getPetList() {
        long id = BaseContext.getCurrentId();
        List<Pet> petList = petService.getPetList(id);
        return R.success(petList);
    }

    @GetMapping("/pet/{petId}")
    public R<Pet> getPet(@PathVariable("petId") long petId) {
        Long id = BaseContext.getCurrentId();
        Pet pet = petService.getPet(petId, id);
        if (pet.getUserId().equals(BaseContext.getCurrentId()) || pet.getPetVisible()) {
            return R.success(pet);
        } else {
            return R.error("No pet found");
        }
    }

    @DeleteMapping("/pet/{petId}")
    public R<String> deletePet(@PathVariable("petId") long petId) {
        long id = BaseContext.getCurrentId();
        petService.deletePet(petId, id);
        return R.success("Delete success");
    }

    @PutMapping("/pet/{petId}")
    public R<String> updatePet(@PathVariable("petId") Long petId, @RequestBody Pet pet) {
        Pet targetPet = petService.getById(petId);
        if (targetPet == null) return R.error("No such pet");
        if (!targetPet.getUserId().equals(BaseContext.getCurrentId())) {
            return R.error("No right to update this pet");
        }
        if (petService.updateById(pet)) {
            return R.success("Update Success");
        } else {
            return R.error("Fail to update");
        }
    }

    @PostMapping("/pet/image/{petId}")
    public R<PetImage> uploadImage(@PathVariable("petId") Long petId, @RequestBody @Validated PetImage petImage) {
        Long currentId = BaseContext.getCurrentId();
        PetImage newPetImage = petService.saveImageForPet(petId, currentId, petImage);
        return R.success(newPetImage);

    }

    @DeleteMapping("/pet/image")
    public R<String> deleteImage(@RequestBody PetImage image) {
        petService.deleteImageForPet(image.getPetId(), BaseContext.getCurrentId(), image.getImageId());
        return R.success("Success to delete this image");

    }
}
