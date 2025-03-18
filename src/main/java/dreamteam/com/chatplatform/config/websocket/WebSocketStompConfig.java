package dreamteam.com.chatplatform.config.websocket;

import dreamteam.com.chatplatform.config.handler.ChatHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketStompConfig implements WebSocketMessageBrokerConfigurer {

  @Override
  public void configureMessageBroker(MessageBrokerRegistry registry) {
    registry.enableSimpleBroker("/topic"); // Enables a simple in-memory broker
    registry.setApplicationDestinationPrefixes("/app"); // Mapping for client messages
  }

  @Override
  public void registerStompEndpoints(StompEndpointRegistry registry) {
    registry.addEndpoint("/ws")  // Endpoint clients will connect to
        .setAllowedOriginPatterns("http://localhost:*") // Allow connections from any origin
        .withSockJS(); // Enable CORS if needed
  }
}
