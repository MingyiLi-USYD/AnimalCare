package usyd.mingyi.animalcare.service;

import com.baomidou.mybatisplus.extension.service.IService;
import usyd.mingyi.animalcare.pojo.Subscription;

import java.util.List;

public interface SubscriptionService extends IService<Subscription> {
    List<String> getAllSubscriptions(Long userId);
   // List<SubscriptionDto> getAllSubscriptions(Long userId);

}
