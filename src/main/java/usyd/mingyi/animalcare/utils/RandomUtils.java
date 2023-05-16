package usyd.mingyi.animalcare.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.LinkedHashMap;

public class RandomUtils {

    public static String getRandomNickname(RestTemplate restTemplate){
        String url = "https://random-data-api.com/api/users/random_user";
       try {
           ResponseEntity<Object> result = restTemplate.getForEntity(url, Object.class);
           System.out.println("-----------------------");
           LinkedHashMap body = (LinkedHashMap) result.getBody();
           String firstName = (String) body.get("first_name");
           return firstName;
       }catch (Exception e){
           return "randomName";
       }

    }
}
