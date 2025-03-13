package dreamteam.com.chatplatform.config;

import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;
import java.util.Collections;
import java.util.Map;

public class JwtTokenFilter implements HandshakeInterceptor {
    private static final String SECRET_KEY = "+h24lqa3+CvwC3HtPdPY6XqI07+65A1c5tghtfEdsNs=";

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                   WebSocketHandler wsHandler, Map<String, Object> attributes) {
        String token = extractToken(request);

        if (token == null || !validateToken(token)) {
            response.setStatusCode(HttpStatus.FORBIDDEN);
            return false;
        }

        String username = extractUsername(token);
        User user = new User(username, "", Collections.singletonList(new SimpleGrantedAuthority("USER")));
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());

        SecurityContextHolder.getContext().setAuthentication(authentication);
        attributes.put("user", user);

        return true;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
                               WebSocketHandler wsHandler, Exception exception) {
    }

    private String extractToken(ServerHttpRequest request) {
        String query = request.getURI().getQuery();
        if (query != null && query.startsWith("token=")) {
            return query.substring(6);
        }
        return null;
    }

    private boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(SECRET_KEY) // Убедись, что `secretKey` совпадает с ключом генерации токенов
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        } // Замени на свою логику
    }

    private String extractUsername(String token) {
        return "TestUser"; // Замени на нормальный парсинг JWT
    }
}
