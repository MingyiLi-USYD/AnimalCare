package usyd.mingyi.animalcare.utils;


import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

public class PasswordUtils {
    private static final PasswordEncoder PASSWORD_ENCODER = new BCryptPasswordEncoder();

    // 加密密码
    public static String hashPassword(String password) {
        return PASSWORD_ENCODER.encode(password);
    }

    // 验证密码是否匹配
    public static boolean verifyPassword(String rawPassword, String encodedPassword) {
        return PASSWORD_ENCODER.matches(rawPassword, encodedPassword);
    }
}
