package dreamteam.com.chatplatform.config.handler;

import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;

public class ChatHandler extends TextWebSocketHandler {

  @Override
  public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
    System.out.println("Received: " + message.getPayload());
    session.sendMessage(new TextMessage("Server: " + message.getPayload()));
  }

  @Override
  public void afterConnectionEstablished(WebSocketSession session) throws Exception {
    System.out.println("New client connected: " + session.getId());
  }

  @Override
  public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
    System.out.println("Client have been disconnected disconnected: " + session.getId());
  }
}
