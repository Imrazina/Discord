package dreamteam.com.chatplatform.controller;

import dreamteam.com.chatplatform.config.ChatConfig;
import dreamteam.com.chatplatform.model.ChatMessage;
import dreamteam.com.chatplatform.model.User;
import dreamteam.com.chatplatform.repository.MessageRepository;
import dreamteam.com.chatplatform.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Map;

@CrossOrigin(origins = "http://localhost:8000")
@Controller
public class ChatController {

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private UserRepository userRepository;

    @MessageMapping("/chat")
    @SendTo("/topic/messages")
    public ChatMessage sendMessage(@Payload Map<String, String> payload, Principal principal) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            System.out.println("SecurityContext пуст или не аутентифицирован!");
            throw new RuntimeException("Пользователь не аутентифицирован");
        } else {
            System.out.println("Пользователь WebSocket-аутентифицирован: " + auth.getName());
        }

        String username = principal.getName(); // Теперь работает
        User sender = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        ChatMessage message = new ChatMessage();
        message.setSender(sender);
        message.setContent(payload.get("content"));
        message.setCreatedAt(LocalDateTime.now());



        messageRepository.save(message);
        return message;
    }
}