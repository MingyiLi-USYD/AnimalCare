package usyd.mingyi.animalcare.mongodb.dao;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import usyd.mingyi.animalcare.pojo.CloudMessage;

@Repository
public interface CloudMessageRepository extends MongoRepository<CloudMessage,String> {
}
