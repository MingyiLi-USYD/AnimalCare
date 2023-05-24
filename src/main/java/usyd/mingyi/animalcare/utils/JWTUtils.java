package usyd.mingyi.animalcare.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import usyd.mingyi.animalcare.pojo.User;

import java.util.Calendar;

public class JWTUtils {

    private static final String SALT = "MingyiLi";
    public static String generateToken(User user){
        Calendar instance = Calendar.getInstance();
        //默认令牌过期时间7天
        instance.add(Calendar.DATE, 7);

        JWTCreator.Builder builder = JWT.create();
        builder.withClaim("userId", user.getId())
                .withClaim("userName", user.getUserName());

        String token = builder.withExpiresAt(instance.getTime())
                .sign(Algorithm.HMAC256(SALT));
        return token;
    }

    public static boolean verify(String token){
        JWTVerifier jwtVerifier = JWT.require(Algorithm.HMAC256(SALT)).build();
        DecodedJWT verify = jwtVerifier.verify(token);
        return true;
    }

    public static DecodedJWT getTokenInfo(String token){
        return JWT.require(Algorithm.HMAC256(SALT)).build().verify(token);
    }


}
