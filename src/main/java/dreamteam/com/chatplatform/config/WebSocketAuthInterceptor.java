package dreamteam.com.chatplatform.config;

import dreamteam.com.chatplatform.config.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class WebSocketAuthInterceptor implements ChannelInterceptor {

    private final JwtTokenProvider jwtTokenProvider;

    @Autowired
    public WebSocketAuthInterceptor(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        if (accessor != null) {
            if (StompCommand.CONNECT.equals(accessor.getCommand())) {
                String token = accessor.getFirstNativeHeader("Authorization");
                System.out.println("📢 Токен при WebSocket-подключении: " + token);

                if (token != null && token.startsWith("Bearer ")) {
                    token = token.substring(7); // Убираем "Bearer "
                    Authentication auth = jwtTokenProvider.getAuthentication(token);
                    if (auth != null) {
                        accessor.setUser(auth);
                        SecurityContextHolder.getContext().setAuthentication(auth);
                        System.out.println("✅ WebSocket аутентифицирован: " + auth.getName());
                    } else {
                        System.out.println("❌ Ошибка аутентификации WebSocket");
                    }
                }
            }
        }
        return message;
    }
}