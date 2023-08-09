package usyd.mingyi.animalcare.service;

import com.baomidou.mybatisplus.extension.service.IService;
import usyd.mingyi.animalcare.pojo.Subscription;

import java.util.List;

public interface SubscriptionService extends IService<Subscription> {
    List<String> getAllSubscribes(Long userId);
    List<String> getAllSubscribers(Long userId);

}
