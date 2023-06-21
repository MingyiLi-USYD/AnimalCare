package usyd.mingyi.animalcare.controller;

import com.github.yulichang.wrapper.MPJLambdaWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import usyd.mingyi.animalcare.common.CustomException;
import usyd.mingyi.animalcare.common.R;
import usyd.mingyi.animalcare.config.ProjectProperties;
import usyd.mingyi.animalcare.pojo.Image;
import usyd.mingyi.animalcare.pojo.Pet;
import usyd.mingyi.animalcare.service.ImageService;
import usyd.mingyi.animalcare.service.PetService;
import usyd.mingyi.animalcare.utils.BaseContext;
import usyd.mingyi.animalcare.utils.ImageUtil;
import usyd.mingyi.animalcare.utils.JWTUtils;
import usyd.mingyi.animalcare.utils.ResultData;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;

@RestController
public class PetController {

    @Autowired
    PetService petService;

    @Autowired
    ProjectProperties projectProperties;

    @Autowired
    ImageService imageService;





    @PostMapping("/pet")
    public R<String> addPet(@RequestParam(value = "avatar") MultipartFile avatar,
                                                  @RequestParam("petName") String petName,
                                                  @RequestParam("category") String category,
                                                  @RequestParam("petDescription") String petDescription,
                                                  @RequestParam("petVisible") boolean petVisible,
                                                  HttpServletRequest request) {
        String userName = JWTUtils.getUserName(request.getHeader("auth"));
        String avatarUrl= ImageUtil.savePetImage(avatar, userName);
        Pet pet = new Pet();
        pet.setPetAvatar(avatarUrl);
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

        if (pet != null) {
            return R.success(pet);
        } else {
            throw new CustomException("no pet found");
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
        if(targetPet.getUserId()!=BaseContext.getCurrentId()){
            return R.error("No right to update this pet");
        }
        pet.setPetId(petId);
        pet.setUserId(BaseContext.getCurrentId());
        System.out.println(pet);
    /*    petService.update(pet,new MPJLambdaWrapper<Pet>().eq(Pet::getUserId,BaseContext.getCurrentId()));
        return null;*/
       if(petService.updateById(pet)){
           return R.success("Update Success");
       }else {
           return R.error("Fail to update");
       }
    }

    @PostMapping("/pet/image/{petId}")
    public R<Image> uploadImage(@RequestParam(value = "image")MultipartFile image,@PathVariable("petId") Long petId, HttpServletRequest request){
        String userName = JWTUtils.getUserName(request.getHeader("auth"));
        String imageName = ImageUtil.savePetImage(image, userName);
        Image imageObj = new Image();
        imageObj.setUrl(imageName);
        imageObj.setPetId(petId);
        imageService.save(imageObj);
        return R.success(imageObj);
    }

    @DeleteMapping ("/pet/image")
    public R<String> deleteImage(@RequestBody Image image){
        long petId = image.getPetId();
        Pet pet = petService.getById(petId);
        System.out.println(pet);
        if(pet==null)return R.error("No such pet");
        if(pet.getUserId()!=BaseContext.getCurrentId()){
            return R.error("No right to delete");
        }else {
            if(imageService.removeById(image.getId())){
                return R.success("Success");
            }else {
                return R.success("No change");
            }
        }


    }
}