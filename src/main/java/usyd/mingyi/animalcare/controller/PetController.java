package usyd.mingyi.animalcare.controller;

import io.netty.util.internal.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import usyd.mingyi.animalcare.config.ProjectProperties;
import usyd.mingyi.animalcare.pojo.Pet;
import usyd.mingyi.animalcare.service.FriendService;
import usyd.mingyi.animalcare.service.PetService;
import usyd.mingyi.animalcare.service.PostService;
import usyd.mingyi.animalcare.service.UserService;
import usyd.mingyi.animalcare.utils.BaseContext;
import usyd.mingyi.animalcare.utils.ImageUtil;
import usyd.mingyi.animalcare.utils.JWTUtils;
import usyd.mingyi.animalcare.utils.ResultData;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
public class PetController {

    @Autowired
    PetService petService;

    @Autowired
    ProjectProperties projectProperties;




    @PostMapping("/pet/newPet")
    public ResponseEntity<Object> addPet(@RequestBody Map map,HttpServletRequest request ) {
        String auth = request.getHeader("auth");
        String userName = JWTUtils.getUserName(auth);
        String fileDiskLocation = projectProperties.fileDiskLocation;
        ;
        String projectPrefix = projectProperties.projectPrefix;
        long id = BaseContext.getCurrentId();
        String data = "";//实体部分数
        String suffix = "";//图片后缀，用以识别哪种格式数据
        String avatarUrl = (String) map.get("avatarUrl");
        String name = (String) map.get("petName");
        String category = (String) map.get("category");
        List<String> list = (List<String>) map.get("petImageListArr");
        String petDescription = (String) map.get("petDescription");
        Pet pet = new Pet();
        pet.setCategory(category);
        pet.setPetName(name);
        pet.setUserId(id);
        pet.setPetDescription(petDescription);

        if (StringUtil.isNullOrEmpty(avatarUrl)) {
            if (category.equals("dog")) {
                pet.setPetImageAddress(projectPrefix + "dogDefault.jpg");
            } else {
                pet.setPetImageAddress(projectPrefix + "catDefault.jpg");
            }


        } else if (ImageUtil.checkImage(avatarUrl)) {
            suffix = ImageUtil.getSuffix(avatarUrl);
            data = ImageUtil.getData(avatarUrl);
            String tempFileName = UUID.randomUUID().toString() + suffix; //文件名
            String path = fileDiskLocation + userName; //文件路径
            try {
                ImageUtil.convertBase64ToFile(data, path, tempFileName);
                pet.setPetImageAddress(projectPrefix + userName + "/" + tempFileName);


            } catch (Exception e) {
                e.printStackTrace();
                return new ResponseEntity<>(ResultData.fail(201, "File invalid"), HttpStatus.CREATED);
            }
        } else {
            return new ResponseEntity<>(ResultData.fail(201, "File invalid"), HttpStatus.CREATED);
        }
        petService.addPet(pet);
        Long petId = pet.getPetId();
        if (list == null) return new ResponseEntity<>(ResultData.success("OK"), HttpStatus.OK);

        for (String base64Data : list) {
            if (ImageUtil.checkImage(base64Data)) {
                suffix = ImageUtil.getSuffix(base64Data);
                data = ImageUtil.getData(base64Data);
            } else {
                return new ResponseEntity<>(ResultData.fail(201, "File invalid"), HttpStatus.CREATED);
            }

            String tempFileName = UUID.randomUUID().toString() + suffix; //文件名

            String path = fileDiskLocation + userName; //文件路径

            try {
                ImageUtil.convertBase64ToFile(data, path, tempFileName);
                petService.addImage(petId, projectPrefix + userName + "/" + tempFileName);
            } catch (Exception e) {
                e.printStackTrace();
                return new ResponseEntity<>(ResultData.fail(201, "File invalid"), HttpStatus.CREATED);

            }
        }

        return new ResponseEntity<>(ResultData.success("OK"), HttpStatus.OK);
    }

    @PostMapping("/pet/android/newPet")
    public ResponseEntity<Object> addPetInAndroid(@RequestParam(value = "images",required = false) MultipartFile[] images,
                                                  @RequestParam(value = "avatar",required = false) MultipartFile avatar,
                                                  @RequestParam("petName") String petName,
                                                  @RequestParam("category") String category,
                                                  @RequestParam("petDescription") String petDescription,
                                                  HttpServletRequest request, HttpSession session) {
        String fileDiskLocation = projectProperties.fileDiskLocation;
        ;
        String projectPrefix = projectProperties.projectPrefix;
        long id = BaseContext.getCurrentId();
        String userName = (String) session.getAttribute("userName");
        Pet pet = new Pet();
        pet.setUserId(id);
        pet.setCategory(category);
        pet.setPetName(petName);
        pet.setPetDescription(petDescription);

        try {
            //存入宠物头像
            String originalName = avatar.getOriginalFilename();
            System.out.println(originalName);
            String suffix = originalName.substring(originalName.lastIndexOf("."));
            String tempFileName = UUID.randomUUID().toString() + suffix; //文件名
            String path = fileDiskLocation + userName; //文件路径
            File newFile = new File(path+ File.separator + tempFileName);
            if(!newFile.getParentFile().exists()){
                newFile.getParentFile().mkdirs();
            }
            avatar.transferTo(newFile);
            pet.setPetImageAddress(projectPrefix + userName + "/" + tempFileName);
        }catch (NullPointerException | IOException e) {
            e.printStackTrace();
        }


        petService.addPet(pet);
        long petId = pet.getPetId();
        try {
            for(int i=0;i<images.length;i++){

                String originalName = images[i].getOriginalFilename();
                System.out.println(originalName);
                String suffix = originalName.substring(originalName.lastIndexOf("."));
                String tempFileName = UUID.randomUUID().toString() + suffix; //文件名
                String path = fileDiskLocation + userName; //文件路径
                petService.addImage(petId, projectPrefix + userName + "/" + tempFileName);
                File newFile = new File(path+ File.separator + tempFileName);
                if(!newFile.getParentFile().exists()){
                    newFile.getParentFile().mkdirs();
                }
                images[i].transferTo(newFile);
            }

        }catch (NullPointerException | IOException e) {
            e.printStackTrace();
        }

        return new ResponseEntity<>(ResultData.success("OK"), HttpStatus.OK);
    }

    @GetMapping("/getPetList")
    public ResponseEntity<Object> getPetList(HttpSession session) {
        int id = (int) session.getAttribute("id");
        List<Pet> petList = petService.getPetList(id);
        return new ResponseEntity<>(ResultData.success(petList), HttpStatus.OK);
    }

    @GetMapping("/pet/{petId}")
    public ResponseEntity<Object> getPet(@PathVariable("petId") int petId) {
        Long id = BaseContext.getCurrentId();
        Pet pet = petService.getPet(petId, id);

        if (pet != null) {
            return new ResponseEntity<>(ResultData.success(pet), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(ResultData.fail(201, "No such pet found"), HttpStatus.CREATED);
        }
    }

    @DeleteMapping("/pet/{petId}")
    public ResponseEntity<Object> deletePet(@PathVariable("petId") int petId, HttpSession session) {
        int id = (int) session.getAttribute("id");
        int i = petService.deletePet(petId, id);
        if (i == 0) return new ResponseEntity<>(ResultData.fail(201, "Fail to delete for no such pet"), HttpStatus.CREATED);
        return new ResponseEntity<>(ResultData.success("OK"), HttpStatus.OK);
    }

    @PutMapping("/pet/{petId}")
    public ResponseEntity<Object> updatePet(@PathVariable("petId") int petId, HttpSession session) {

        return null;
    }

}
