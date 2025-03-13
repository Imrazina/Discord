package dreamteam.com.chatplatform.config;

import dreamteam.com.chatplatform.model.ChatMessage;
import dreamteam.com.chatplatform.model.User;
import dreamteam.com.chatplatform.repository.MessageRepository;
import dreamteam.com.chatplatform.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Service
@Component
public class ChatConfig {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MessageRepository chatMessageRepository;

    public void processMessage(ChatMessage chatMessage) {
        User sender = userRepository.findByUsername(chatMessage.getSender().getUsername())
                .orElseGet(() -> {
                    User newUser = new User();
                    newUser.setUsername(chatMessage.getSender().getUsername());
                    return userRepository.save(newUser); // Сохраняем перед использованием
                });

        chatMessage.setSender(sender); // Теперь sender гарантированно сохранён
        chatMessageRepository.save(chatMessage); // Теперь можно сохранять сообщение
    }
}
