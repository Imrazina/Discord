package dreamteam.com.chatplatform.controller;

import dreamteam.com.chatplatform.model.ChatMessage;
import dreamteam.com.chatplatform.model.UserEntity;
import dreamteam.com.chatplatform.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Map;

@Controller
public class ChatController {

    private static final Logger logger = LoggerFactory.getLogger(ChatController.class);
    private final UserRepository userRepository;

    public ChatController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @MessageMapping("/chat")
    @SendTo("/topic/messages")
    public ChatMessage sendMessage(@Payload Map<String, String> payload, Principal principal) {
        if (principal == null) {
            logger.error("User is not authenticated");
            throw new RuntimeException("User is not authenticated");
        }

        String username = principal.getName();
        logger.info("Received message from user: {}", username);

        UserEntity sender = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        ChatMessage message = new ChatMessage();
        message.setSender(sender);
        message.setContent(payload.get("content"));
        message.setCreatedAt(LocalDateTime.now());

        logger.info("Message sent: {}", message.getContent());
        return message;
    }
}
