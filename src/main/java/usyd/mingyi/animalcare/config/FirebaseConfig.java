package usyd.mingyi.animalcare.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.io.InputStream;

@Configuration
public class FirebaseConfig {
    @Value("${firebase.config.file}")
    private Resource resource;

    @Bean
    public FirebaseApp firebaseApp() throws IOException {

        InputStream serviceAccount = resource.getInputStream();

        FirebaseOptions options = new FirebaseOptions.Builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .setDatabaseUrl("https://petbook-react-springboot-default-rtdb.asia-southeast1.firebasedatabase.app")
                .build();

       return FirebaseApp.initializeApp(options);
    }


}
