package usyd.mingyi.animalcare.service.serviceImp;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import usyd.mingyi.animalcare.mapper.SubscriptionMapper;
import usyd.mingyi.animalcare.pojo.Subscription;
import usyd.mingyi.animalcare.service.SubscriptionService;

import java.util.List;
import java.util.Objects;

@Service
public class SubscriptionServiceImp extends ServiceImpl<SubscriptionMapper,Subscription> implements SubscriptionService  {
    @Autowired
    SubscriptionMapper subscriptionMapper;
    @Override
    public List<String> getAllSubscribes(Long userId) {
        return this.getSubscriptions(Subscription::getMyId,userId);

        /*return subscriptionMapper.selectList(
                new LambdaQueryWrapper<Subscription>().eq(Subscription::getMyId,userId)
                )
                .stream().map(Subscription::getSubscribedUserId)
                .map(String::valueOf).toList();*/
    }

    @Override
    public List<String> getAllSubscribers(Long userId) {
        return this.getSubscriptions(Subscription::getSubscribedUserId,userId);

/*        return subscriptionMapper.selectList(
                        new LambdaQueryWrapper<Subscription>().eq(Subscription::getSubscribedUserId,userId)
                )
                .stream().map(Subscription::getSubscribedUserId)
                .map(String::valueOf).toList();*/
    }

    private List<String> getSubscriptions(SFunction<Subscription, ?> field,Long userId){
        return subscriptionMapper.selectList(
                        new LambdaQueryWrapper<Subscription>().eq(field,userId)
                )
                .stream().map(Subscription::getSubscribedUserId)
                .map(String::valueOf).toList();
    }
}
