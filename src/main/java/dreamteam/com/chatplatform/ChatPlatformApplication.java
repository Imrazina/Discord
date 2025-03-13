package dreamteam.com.chatplatform;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SpringBootApplication
public class ChatPlatformApplication {
    public static void main(String[] args) {
        SpringApplication.run(ChatPlatformApplication.class, args);
    }
}
