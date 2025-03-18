package dreamteam.com.chatplatform.controller;

import dreamteam.com.chatplatform.model.ChatMessage;
import dreamteam.com.chatplatform.model.User;
import dreamteam.com.chatplatform.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Map;

@CrossOrigin(origins = "http://localhost:8000")
@Controller
public class ChatController {

    @Autowired
    private UserRepository userRepository; // Добавьте репозиторий

    @MessageMapping("/chat")
    @SendTo("/topic/messages")
    public ChatMessage sendMessage(@Payload Map<String, String> payload, Principal principal) {
        if (principal == null) {
            System.out.println("SecurityContext пуст или не аутентифицирован!");
            throw new RuntimeException("Пользователь не аутентифицирован");
        }

        String username = principal.getName();
        System.out.println("Сообщение от " + username + ": " + payload.get("content"));

        // Находим пользователя по имени
        User sender = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        ChatMessage message = new ChatMessage();
        message.setSender(sender); // Передаем объект User
        message.setContent(payload.get("content"));
        message.setCreatedAt(LocalDateTime.now());

        return message;
    }
}