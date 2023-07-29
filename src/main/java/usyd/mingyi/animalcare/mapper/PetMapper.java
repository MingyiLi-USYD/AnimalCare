package usyd.mingyi.animalcare.mapper;

import com.github.yulichang.base.MPJBaseMapper;
import org.apache.ibatis.annotations.Mapper;
import usyd.mingyi.animalcare.pojo.Pet;

import java.util.List;
import java.util.Map;

@Mapper
public interface PetMapper extends MPJBaseMapper<Pet> {
}
