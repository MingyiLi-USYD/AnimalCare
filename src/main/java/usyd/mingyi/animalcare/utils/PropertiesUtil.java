package usyd.mingyi.animalcare.utils;

import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.util.Properties;

public class PropertiesUtil {
    public static Properties readProperties(String configFile){
        Properties properties = new Properties();
        try {
            properties.load( new ClassPathResource(configFile).getInputStream());
        }catch (IOException e) {
            e.printStackTrace();
        }
        return properties;
    }
}
