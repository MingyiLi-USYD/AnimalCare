package usyd.mingyi.animalcare.utils;

import net.jodah.expiringmap.ExpirationPolicy;
import net.jodah.expiringmap.ExpiringMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Random;
import java.util.concurrent.TimeUnit;


public class Verification {

    // use Redis to replace local memory storage which may lead to JVM OOM
    @Deprecated
    private static ExpiringMap<Object, String> CodeCache = ExpiringMap.builder()
            .maxSize(200)
            .expiration(5, TimeUnit.MINUTES)
            .expirationPolicy(ExpirationPolicy.ACCESSED)
            .variableExpiration()
            .build();
    @Deprecated
    public static void putCode(String userName,String code){
        CodeCache.put(userName,code);
    }
    @Deprecated
    public static boolean hasUser(String userName){
        return CodeCache.containsKey(userName);
    }
    @Deprecated
    public static String getCode(String code){
        return CodeCache.get(code);
    }


}
