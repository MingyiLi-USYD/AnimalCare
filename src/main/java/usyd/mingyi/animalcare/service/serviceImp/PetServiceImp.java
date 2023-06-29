package usyd.mingyi.animalcare.service.serviceImp;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import usyd.mingyi.animalcare.common.CustomException;
import usyd.mingyi.animalcare.mapper.PetMapper;
import usyd.mingyi.animalcare.pojo.Pet;
import usyd.mingyi.animalcare.service.PetService;

import java.util.List;

@Service
public class PetServiceImp extends ServiceImpl<PetMapper,Pet> implements PetService {

    @Autowired
    PetMapper petMapper;

    @Override
    public List<Pet> getPetList(long userId) {
        MPJLambdaWrapper<Pet> wrapper = new MPJLambdaWrapper<>();
        wrapper.eq(Pet::getUserId,userId);
        return petMapper.selectList(wrapper);

    }

    @Override
    public Pet getPet(long petId, long useId) {
        Pet pet = petMapper.getPet(petId, useId);
        if(pet.getUserId()==useId){
            return pet;
        }else if(pet.isPetVisible()){
            return pet;
        }else {
            throw new CustomException("No such pet");
        }
    }

    @Override
    public int deletePet(long petId, long useId) {
        return petMapper.deletePet(petId, useId);
    }


}
