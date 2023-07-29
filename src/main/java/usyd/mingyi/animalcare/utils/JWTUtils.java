package usyd.mingyi.animalcare.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import usyd.mingyi.animalcare.common.CustomException;
import usyd.mingyi.animalcare.pojo.User;

import java.util.Calendar;

public class JWTUtils {

    private static final String SALT = "MingyiLi";
    public static String generateToken(User user){
        Calendar instance = Calendar.getInstance();
        //默认令牌过期时间7天
        instance.add(Calendar.DATE, 90);

        JWTCreator.Builder builder = JWT.create();
        builder.withClaim("userId", user.getUserId())
                .withClaim("username", user.getUsername())
                .withClaim("role",user.getRole());

        String token = builder.withExpiresAt(instance.getTime())
                .sign(Algorithm.HMAC256(SALT));
        return token;
    }

    public static boolean verify(String token){
        try {
            JWTVerifier jwtVerifier = JWT.require(Algorithm.HMAC256(SALT)).build();
            DecodedJWT verify = jwtVerifier.verify(token);

        }catch (Exception e){
            throw new CustomException("token error");
        }
         return true;
    }

    public static boolean verifyInSocket(String token){
        try {
            JWTVerifier jwtVerifier = JWT.require(Algorithm.HMAC256(SALT)).build();
            DecodedJWT verify = jwtVerifier.verify(token);

        }catch (Exception e){
            return false;
        }
        return true;
    }

    public static DecodedJWT getTokenInfo(String token){
        return JWT.require(Algorithm.HMAC256(SALT)).build().verify(token);
    }

    public static String getUserName(String token){
        return JWT.require(Algorithm.HMAC256(SALT)).build().verify(token).getClaim("username").asString();
    }

    public static String generateFirebaseToken(String userId)   {

        try {

            return FirebaseAuth.getInstance().createCustomToken(userId);
        } catch (FirebaseAuthException e) {
            throw new RuntimeException(e);
        }

    }

}
