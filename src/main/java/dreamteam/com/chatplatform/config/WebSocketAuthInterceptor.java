package dreamteam.com.chatplatform.config;

import dreamteam.com.chatplatform.jwt.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

@Component
public class WebSocketAuthInterceptor implements ChannelInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(WebSocketAuthInterceptor.class);
    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;

    public WebSocketAuthInterceptor(JwtUtil jwtUtil, UserDetailsService userDetailsService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }


    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        if (accessor == null || StompCommand.CONNECT != accessor.getCommand()) {
            return message;
        }
        System.out.println("🔒 SecurityContext user: " + SecurityContextHolder.getContext().getAuthentication());

        System.out.println("📡 WebSocket Headers: " + accessor.toNativeHeaderMap());

        List<String> authHeaders = accessor.getNativeHeader("Authorization");
        if (authHeaders == null || authHeaders.isEmpty()) {
            System.out.println("❌ WebSocketAuthInterceptor: Authorization header is missing");
            return message;
        }

        String token = authHeaders.get(0).replace("Bearer ", "");
        System.out.println("🔑 Extracted token: " + token);

// 👉 Добавляем ЛОГ перед вызовом extractUsername
        System.out.println("📢 Calling extractUsername with token: " + token);
        String username = jwtUtil.extractUsername(token);

        if (username == null) {
            System.out.println("❌ Invalid token: Username is null");
            return message;
        }
        System.out.println("🔒 SecurityContext user: " + SecurityContextHolder.getContext().getAuthentication());


        System.out.println("🔍 Extracted username: " + username);  // ✅ Должно появиться в логах

        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        if (userDetails == null) {
            System.out.println("❌ User not found: " + username);
            return message;
        }

        System.out.println("✅ Found user in database: " + userDetails.getUsername());  // ✅ Должно появиться в логах

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        accessor.setUser(authentication);

        System.out.println("✅ WebSocket Authenticated user: " + username);
        return message;
    }

}
