package usyd.mingyi.animalcare.service.serviceImp;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import usyd.mingyi.animalcare.mapper.ImageMapper;
import usyd.mingyi.animalcare.pojo.Image;
import usyd.mingyi.animalcare.service.ImageService;

@Service
public class ImageServiceImp extends ServiceImpl<ImageMapper, Image>implements ImageService {
}
