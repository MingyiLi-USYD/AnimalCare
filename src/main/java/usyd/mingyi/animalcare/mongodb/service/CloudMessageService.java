package usyd.mingyi.animalcare.mongodb.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.TypedAggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import usyd.mingyi.animalcare.common.CustomException;
import usyd.mingyi.animalcare.mongodb.dao.CloudMessageRepository;
import usyd.mingyi.animalcare.mongodb.entity.CloudMessage;
import usyd.mingyi.animalcare.pojo.User;
import usyd.mingyi.animalcare.service.UserService;
import usyd.mingyi.animalcare.socketEntity.ChatMessage;
import usyd.mingyi.animalcare.utils.CommonUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;



@Service
public class CloudMessageService {

    private final CloudMessageRepository cloudMessageRepository;
    private final MongoTemplate mongoTemplate;

    @Autowired
    private UserService userService;

    @Autowired
    public CloudMessageService(CloudMessageRepository cloudMessageRepository, MongoTemplate mongoTemplate) {
        this.cloudMessageRepository = cloudMessageRepository;
        this.mongoTemplate = mongoTemplate;
    }

    public void insertMsg(ChatMessage chatMessage){
        String combinedId = CommonUtils.combineId(chatMessage.getFromId(),chatMessage.getToId());
        if(existsCloudMessageById(combinedId)){
            Query query = Query.query(Criteria.where("id").is(combinedId));
            Update update = new Update()
                    .set("latestTime",chatMessage.getDate())
                    .push("chatList", chatMessage).inc("unRead",1L);
            mongoTemplate.updateFirst(query, update, CloudMessage.class);
        }else {
            List<ChatMessage> chatList = new ArrayList<>(1);
            chatList.add(chatMessage);
            CloudMessage newCloudMessage = new CloudMessage();
            newCloudMessage.setId(combinedId);
            newCloudMessage.setLatestTime(System.currentTimeMillis());
            newCloudMessage.setChatList(chatList);
            newCloudMessage.setParticipates(CommonUtils.combineParticipates(chatMessage.getFromId(),chatMessage.getToId()));
            newCloudMessage.setUnRead(1L);
            cloudMessageRepository.insert(newCloudMessage);
        }
    }

    public CloudMessage getCloudMessageById(String id) {

        Optional<CloudMessage> optionalCloudMessage = cloudMessageRepository.findById(id);
        if (optionalCloudMessage.isEmpty()) {
            throw new CustomException("No chat records found");
        }
        return optionalCloudMessage.orElse(new CloudMessage());
    }

    public boolean existsCloudMessageById(String id) {

        Query query = Query.query(Criteria.where("id").is(id));
        return mongoTemplate.exists(query, CloudMessage.class);
    }

    public List<CloudMessage> getChatRecords(String userId){

        AggregationOperation matchOperation = Aggregation.match(Criteria.where("id").regex(userId));
        AggregationOperation sortOperation = Aggregation.sort(Sort.Direction.DESC, "latestTime");
        AggregationOperation limitOperation = Aggregation.limit(10);
        AggregationOperation projectOperation  = Aggregation.project(CloudMessage.class).and("chatList").slice(-100);
        TypedAggregation<CloudMessage> aggregation = Aggregation.newAggregation(CloudMessage.class, matchOperation,sortOperation,limitOperation, projectOperation);
        List<CloudMessage> cloudMessages = mongoTemplate.aggregate(aggregation, CloudMessage.class).getMappedResults();
        cloudMessages.forEach(cloudMessage -> {
            List<String> participates = cloudMessage.getParticipates();
            for (int i = 0; i < participates.size(); i++) {
                if(participates.get(i)!=null&&!participates.get(i).equals(userId)){
                    User user = userService.getBasicUserInfoById(Long.valueOf(participates.get(i)));
                    cloudMessage.setChatUser(user);
                    break;
                }
            }
        });
        return cloudMessages;
    }



}
