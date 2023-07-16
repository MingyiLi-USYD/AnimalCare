package usyd.mingyi.animalcare.controller;

import lombok.extern.slf4j.Slf4j;
import org.apache.lucene.util.RamUsageEstimator;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import usyd.mingyi.animalcare.common.R;
import usyd.mingyi.animalcare.component.ClientCache;
import usyd.mingyi.animalcare.mongodb.entity.CloudMessage;

import javax.annotation.Resource;

@RestController
@Slf4j
@RequestMapping("/common")
public class CommonController {
    @Resource
    ClientCache clientCache;

    @Resource
    MongoTemplate mongoTemplate;
    @GetMapping("/memo")
    public R<Long> getObjectSize(){

        long size = RamUsageEstimator.sizeOf(clientCache);
        System.out.println(size);
        return null;
    }

    @GetMapping("/mongo")
    public R<Long> testDb(){
        CloudMessage cloudMessage = new CloudMessage();
        cloudMessage.setLastTime(System.currentTimeMillis());
        mongoTemplate.insert(cloudMessage);
        mongoTemplate.insert(cloudMessage);
        mongoTemplate.insert(cloudMessage);
        return null;
    }
}
