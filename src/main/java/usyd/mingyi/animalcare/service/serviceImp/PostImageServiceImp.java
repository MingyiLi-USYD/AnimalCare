package usyd.mingyi.animalcare.service.serviceImp;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import usyd.mingyi.animalcare.mapper.PetImageMapper;
import usyd.mingyi.animalcare.mapper.PostImageMapper;
import usyd.mingyi.animalcare.pojo.PetImage;
import usyd.mingyi.animalcare.pojo.PostImage;
import usyd.mingyi.animalcare.service.PostImageService;


@Service
public class PostImageServiceImp extends ServiceImpl<PostImageMapper, PostImage> implements PostImageService {
}
