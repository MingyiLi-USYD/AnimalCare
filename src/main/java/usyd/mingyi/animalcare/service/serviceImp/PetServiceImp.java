package usyd.mingyi.animalcare.service.serviceImp;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import usyd.mingyi.animalcare.common.CustomException;
import usyd.mingyi.animalcare.dto.PetDto;
import usyd.mingyi.animalcare.mapper.PetImageMapper;
import usyd.mingyi.animalcare.mapper.PetMapper;
import usyd.mingyi.animalcare.pojo.Pet;
import usyd.mingyi.animalcare.pojo.PetImage;
import usyd.mingyi.animalcare.pojo.Post;
import usyd.mingyi.animalcare.service.PetService;
import usyd.mingyi.animalcare.utils.BaseContext;

import java.util.List;

@Service
public class PetServiceImp extends ServiceImpl<PetMapper,Pet> implements PetService {

    @Autowired
    PetMapper petMapper;

    @Autowired
    PetImageMapper petImageMapper;

    @Override
    public List<Pet> getPetList(Long userId) {
        MPJLambdaWrapper<Pet> wrapper = new MPJLambdaWrapper<>();
        wrapper.eq(Pet::getUserId,userId);
        return petMapper.selectList(wrapper);
    }

    @Override
    public Pet getPet(Long petId, Long useId) {
        MPJLambdaWrapper<Pet> query = new MPJLambdaWrapper<>();
        query.selectAll(Pet.class)
                .selectCollection(PetImage.class, PetDto::getPetImage)
                .leftJoin(PetImage.class,PetImage::getPetId,Pet::getPetId)
                .eq(Pet::getPetId,petId);
        return petMapper.selectJoinOne(PetDto.class, query);
    }

    @Override
    public void deletePet(Long petId, Long useId) {
        Pet pet = petMapper.selectById(petId);
        if(pet==null){
            throw new CustomException("No such pet found");
        }
        if(!pet.getUserId().equals(useId)){
            throw new CustomException("No right to delete the pet");
        }
        petMapper.deleteById(petId);
    }

    @Override
    public PetImage saveImageForPet(Long petId, Long userId, PetImage petImage) {
        Pet pet = petMapper.selectById(petId);
        if(pet==null){
            throw new CustomException("No such pet found");
        } else if (!pet.getUserId().equals(userId)) {
            throw new CustomException("No right to upload image for the pet");
        }else {
            petImage.setPetId(petId);
            petImageMapper.insert(petImage);
            return petImage;
        }
    }

    @Override
    public void deleteImageForPet(Long petId, Long userId, Long imageId) {
        //因为mybatis 不支持optional 需要手动判断空
        PetImage petImage = petImageMapper.selectById(imageId);
        if(petImage==null){
            throw new CustomException("No such image found");
        }
        if(!petImage.getPetId().equals(petId)){
            throw new CustomException("Image is not belong to this pet");
        }
        Pet pet = petMapper.selectById(petId);
        //肯定存在这个pet 因为有级联关系不用判断是否为空
        if(!pet.getUserId().equals(userId)){
            throw new CustomException("No right to delete image for the pet");
        }
        petImageMapper.deleteById(imageId);
        //然后把firebase云端上的图片删掉
    }

}
