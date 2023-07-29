package usyd.mingyi.animalcare.service.serviceImp;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import usyd.mingyi.animalcare.mapper.PetImageMapper;
import usyd.mingyi.animalcare.pojo.PetImage;
import usyd.mingyi.animalcare.service.PetImageService;

@Service
public class PetImageServiceImp extends ServiceImpl<PetImageMapper, PetImage> implements PetImageService {
}
