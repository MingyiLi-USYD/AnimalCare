package usyd.mingyi.animalcare.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import usyd.mingyi.animalcare.common.CustomException;
import usyd.mingyi.animalcare.common.R;
import usyd.mingyi.animalcare.pojo.Pet;
import usyd.mingyi.animalcare.pojo.PetImage;
import usyd.mingyi.animalcare.service.PetImageService;
import usyd.mingyi.animalcare.service.PetService;
import usyd.mingyi.animalcare.service.PostImageService;
import usyd.mingyi.animalcare.utils.BaseContext;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
public class PetController {

    @Autowired
    PetService petService;


    @Autowired
    PetImageService petImageService;





    @PostMapping("/pet")
    public R<String> addPet(@RequestParam("petName") String petName,
                            @RequestParam("category") String category,
                            @RequestParam("petDescription") String petDescription,
                            @RequestParam("petVisible") boolean petVisible,
                            @RequestParam("image") String avatar,
                            HttpServletRequest request) {
        Pet pet = new Pet();
        pet.setPetAvatar(avatar);
        pet.setPetDescription(petDescription);
        pet.setPetName(petName);
        pet.setCategory(category);
        pet.setUserId(BaseContext.getCurrentId());
        pet.setPetVisible(petVisible);
        petService.save(pet);
        return R.success("成功了");

    }

    @GetMapping("/getPetList")
    public R<List<Pet>> getPetList() {
        long id = BaseContext.getCurrentId();
        List<Pet> petList = petService.getPetList(id);
        return R.success(petList);
    }

    @GetMapping("/pet/{petId}")
    public R<Pet> getPet(@PathVariable("petId") long petId) {
        Long id = BaseContext.getCurrentId();
        Pet pet = petService.getPet(petId, id);
        if(pet.getUserId().equals(BaseContext.getCurrentId())||pet.getPetVisible()){
            return R.success(pet);
        }else {
            return R.error("No pet found");
        }
    }

    @DeleteMapping("/pet/{petId}")
    public R<String> deletePet(@PathVariable("petId") long petId) {
        long id = BaseContext.getCurrentId();
        int i = petService.deletePet(petId, id);
        if (i == 0) return R.error("Fail to delete for no such pet");
        return R.success("Delete success");
    }

    @PutMapping("/pet/{petId}")
    public R<String> updatePet(@PathVariable("petId") long petId,@RequestBody Pet pet) {
        Pet targetPet = petService.getById(petId);
        if(targetPet==null) return R.error("No such pet");
        if(!targetPet.getUserId().equals(BaseContext.getCurrentId())){
            return R.error("No right to update this pet");
        }
        if(petService.updateById(pet)){
            return R.success("Update Success");
        }else {
            return R.error("Fail to update");
        }
    }

    @PostMapping("/pet/image/{petId}")
    public R<PetImage> uploadImage(@PathVariable("petId") Long petId, @RequestParam String imageUrl){
        System.out.println(imageUrl);
        PetImage imageObj = new PetImage();
       // imageObj.setUrl(imageUrl);
        imageObj.setPetId(petId);
        petImageService.save(imageObj);
        return R.success(imageObj);
    }

    @DeleteMapping ("/pet/image")
    public R<String> deleteImage(@RequestBody PetImage image){
        long petId = image.getPetId();
        Pet pet = petService.getById(petId);
        System.out.println(pet);
        if(pet==null)return R.error("No such pet");

        if(!pet.getUserId().equals(BaseContext.getCurrentId())){
            return R.error("No right to delete");
        }else {
            if(petImageService.removeById(image.getImageId())){
                return R.success("Success");
            }else {
                return R.success("No change");
            }
        }


    }
}
