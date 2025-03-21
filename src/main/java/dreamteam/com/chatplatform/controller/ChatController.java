package dreamteam.com.chatplatform.controller;

import dreamteam.com.chatplatform.model.ChatMessage;
import dreamteam.com.chatplatform.model.UserEntity;
import dreamteam.com.chatplatform.repository.MessageRepository;
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
    private final MessageRepository messageRepository;  // ✅ Подключаем репозиторий сообщений

    public ChatController(UserRepository userRepository, MessageRepository messageRepository) {
        this.userRepository = userRepository;
        this.messageRepository = messageRepository;
    }

    @MessageMapping("/chat")
    @SendTo("/topic/messages")
    public ChatMessage sendMessage(@Payload Map<String, String> payload, Principal principal) {
        if (principal == null) {
            logger.error("❌ User is not authenticated!");
            throw new RuntimeException("User is not authenticated");
        }

        String username = principal.getName();
        logger.info("🟢 Received message from user: {}", username);

        // ✅ Логируем загрузку sender
        UserEntity sender = userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    logger.error("❌ User not found in DB: {}", username);
                    return new RuntimeException("User not found");
                });

        logger.info("✅ Sender loaded from DB: {} (id: {})", sender.getUsername(), sender.getId());

        // ✅ Логируем загрузку receiver
        String receiverUsername = payload.get("receiver");
        UserEntity receiver = null;
        if (receiverUsername != null && !receiverUsername.isEmpty()) {
            receiver = userRepository.findByUsername(receiverUsername)
                    .orElseThrow(() -> {
                        logger.error("❌ Receiver not found in DB: {}", receiverUsername);
                        return new RuntimeException("Receiver not found");
                    });
            logger.info("✅ Receiver loaded from DB: {} (id: {})", receiver.getUsername(), receiver.getId());
        }

        // ✅ Создаем сообщение
        ChatMessage message = new ChatMessage();
        message.setSender(sender);
        message.setReceiver(receiver);
        message.setContent(payload.get("content"));
        message.setCreatedAt(LocalDateTime.now());

        logger.info("📝 Creating new message: sender={}, receiver={}, content='{}'",
                sender.getUsername(),
                (receiver != null ? receiver.getUsername() : "null"),
                message.getContent());

        // ✅ Логируем перед сохранением
        try {
            message = messageRepository.save(message);
            logger.info("✅ Message saved to DB with id: {}", message.getId());
        } catch (Exception e) {
            logger.error("❌ Error saving message to DB!", e);
            throw e;
        }

        return message;
    }
}
