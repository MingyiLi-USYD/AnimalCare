package usyd.mingyi.animalcare.service.serviceImp;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import usyd.mingyi.animalcare.common.CustomException;
import usyd.mingyi.animalcare.dto.PetDto;
import usyd.mingyi.animalcare.mapper.PetMapper;
import usyd.mingyi.animalcare.pojo.Pet;
import usyd.mingyi.animalcare.pojo.PetImage;
import usyd.mingyi.animalcare.service.PetService;

import java.util.List;

@Service
public class PetServiceImp extends ServiceImpl<PetMapper,Pet> implements PetService {

    @Autowired
    PetMapper petMapper;

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
    public int deletePet(Long petId, Long useId) {
        return 0;
    }


}
