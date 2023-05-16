package usyd.mingyi.animalcare.mapper;

import org.apache.ibatis.annotations.Mapper;
import usyd.mingyi.animalcare.pojo.Pet;

import java.util.List;
import java.util.Map;

@Mapper
public interface PetMapper {
    /** 
    * @Description: 添加宠物到对应的用户 
    * @Param: [pet] 
    * @return: int 
    * @Author: Mingyi Li
    * @Date: 2022/10/5 
    */ 
    int addPet(Pet pet);
    /** 
    * @Description: 根据用户id获取到用户所有的宠物 
    * @Param: [userId] 
    * @return: java.util.List<usyd.mingyi.animalcare.pojo.Pet> 
    * @Author: Mingyi Li
    * @Date: 2022/10/5 
    */ 
    List<Pet> getPetList(int userId);
    /** 
    * @Description: 通过宠物id和用户id获取到对应用户的对应宠物的具体信息 
    * @Param: [petId, useId] 
    * @return: usyd.mingyi.animalcare.pojo.Pet 
    * @Author: Mingyi Li
    * @Date: 2022/10/5 
    */ 
    Pet getPet(int petId,int useId);
    /** 
    * @Description: 删除对应宠物id的宠物 
    * @Param: [petId, useId] 
    * @return: int 
    * @Author: Mingyi Li
    * @Date: 2022/10/5 
    */ 
    int deletePet(int petId,int useId);
    /** 
    * @Description: 保存宠物除了头像之外的所有个性化图片 
    * @Param: [imagePetId, imageUrl] 
    * @return: int 
    * @Author: Mingyi Li
    * @Date: 2022/10/5 
    */ 
    int addImage(int imagePetId,String imageUrl);
    /** 
    * @Description: 测试而已无意义
    * @Param: [petId, useId] 
    * @return: java.util.Map<java.lang.String,java.lang.Object> 
    * @Author: Mingyi Li
    * @Date: 2022/10/5 
    */ 
    Map<String,Object> myTest (int petId,int useId);

}
