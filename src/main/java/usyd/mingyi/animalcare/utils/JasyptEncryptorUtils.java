package usyd.mingyi.animalcare.utils;

import org.jasypt.properties.PropertyValueEncryptionUtils;
import org.jasypt.util.text.BasicTextEncryptor;


//use the default config of jasypt, not need to config it in application.yml
public final class JasyptEncryptorUtils {


    private static final String salt = "lybgeek";

    private static BasicTextEncryptor basicTextEncryptor = new BasicTextEncryptor();

    static {
        basicTextEncryptor.setPassword(salt);
    }

    private JasyptEncryptorUtils(){}

    /**
     * 明文加密
     * @param plaintext
     * @return
     */
    public static String encode(String plaintext){
        System.out.println("plaintext string:" + plaintext);
        String ciphertext = basicTextEncryptor.encrypt(plaintext);
        System.out.println("encrypted string:" + ciphertext);
        return ciphertext;
    }

    /**
     * 解密
     * @param ciphertext
     * @return
     */
    public static String decode(String ciphertext){
        System.out.println("encrypted string:" + ciphertext);
        ciphertext = "ENC(" + ciphertext + ")";
        if (PropertyValueEncryptionUtils.isEncryptedValue(ciphertext)){
            String plaintext = PropertyValueEncryptionUtils.decrypt(ciphertext,basicTextEncryptor);
            System.out.println("decrypted string:" + plaintext);
            return plaintext;
        }
        System.out.println("Decryption failed");
        return "";
    }
}