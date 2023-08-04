package usyd.mingyi.animalcare.service.serviceImp;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import usyd.mingyi.animalcare.mapper.SubscriptionMapper;
import usyd.mingyi.animalcare.pojo.Subscription;
import usyd.mingyi.animalcare.service.SubscriptionService;

import java.util.List;

@Service
public class SubscriptionServiceImp extends ServiceImpl<SubscriptionMapper,Subscription> implements SubscriptionService  {
    @Autowired
    SubscriptionMapper subscriptionMapper;
    @Override
    public List<String> getAllSubscriptions(Long userId) {
        MPJLambdaWrapper<Subscription> query = new MPJLambdaWrapper<>();
        query.selectAll(Subscription.class).eq(Subscription::getMyId,userId);
        return subscriptionMapper.selectList(query)
                .stream().map(Subscription::getSubscribedUserId)
                .map(String::valueOf).toList();
    }
}
