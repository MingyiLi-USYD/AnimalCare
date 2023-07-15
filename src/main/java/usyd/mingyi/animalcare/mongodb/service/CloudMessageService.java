package usyd.mingyi.animalcare.mongodb.service;

import com.mongodb.client.result.UpdateResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import usyd.mingyi.animalcare.mongodb.dao.CloudMessageRepository;
import usyd.mingyi.animalcare.pojo.CloudMessage;
import usyd.mingyi.animalcare.socketEntity.ChatMessage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CloudMessageService {

    private final CloudMessageRepository cloudMessageRepository;
    private final MongoTemplate mongoTemplate;

    @Autowired
    public CloudMessageService(CloudMessageRepository cloudMessageRepository, MongoTemplate mongoTemplate) {
        this.cloudMessageRepository = cloudMessageRepository;
        this.mongoTemplate = mongoTemplate;
    }

    public void insertMsg(String currentId,String toId, ChatMessage chatMessage){

        String[] ids = {currentId, toId};
        Arrays.sort(ids);
        String combinedId = String.join("_", ids);
        if(existsCloudMessageById(combinedId)){
            Query query = Query.query(Criteria.where("id").is(combinedId));
            Update update = new Update()
                    .addToSet("lastTime",chatMessage.getDate())
                    .push("chatHistory", chatMessage);
            mongoTemplate.updateFirst(query, update, CloudMessage.class);
        }else {

            List<ChatMessage> chatHistory = new ArrayList<>(1);
            chatHistory.add(chatMessage);
            CloudMessage newCloudMessage = new CloudMessage();
            newCloudMessage.setLastTime(System.currentTimeMillis());
            newCloudMessage.setChatHistory(chatHistory);
            newCloudMessage.setParticipates(Arrays.stream(ids).collect(Collectors.toList()));
            cloudMessageRepository.insert(newCloudMessage);
        }
    }

    public CloudMessage getCloudMessageById(String id) {

        Optional<CloudMessage> optionalCloudMessage = cloudMessageRepository.findById(id);
        return optionalCloudMessage.orElse(null);
    }

    public boolean existsCloudMessageById(String id) {

        Query query = Query.query(Criteria.where("id").is(id));
        return mongoTemplate.exists(query, CloudMessage.class);
    }


}
