package dreamteam.com.chatplatform.controller;

import dreamteam.com.chatplatform.model.PushSubscription;
import dreamteam.com.chatplatform.repository.PushSubscriptionRepository;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/push")
@CrossOrigin(origins = "http://localhost:8000", allowCredentials = "true")
public class PushSubscriptionController {

    @Autowired
    private PushSubscriptionRepository repository;

    @PostMapping("/subscribe")
    public ResponseEntity<?> subscribe(
            @RequestBody Map<String, Object> request,
            Authentication authentication
    ) {
        if (authentication == null || !authentication.isAuthenticated()) {
            System.out.println(" Unauthenticated access attempt");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        System.out.println(" Authenticated user: " + authentication.getName());

        Map<String, Object> keys = (Map<String, Object>) request.get("keys");
        String endpoint = (String) request.get("endpoint");
        String auth = (String) keys.get("auth");
        String p256dh = (String) keys.get("p256dh");
        String username = (String) request.get("username");

        System.out.println("📝 Данные подписки: " +
                "username=" + username + ", " +
                "endpoint=" + endpoint.substring(0, 30) + "...");

        PushSubscription subscription = PushSubscription.builder()
                .endpoint(endpoint)
                .auth(auth)
                .p256dh(p256dh)
                .username(username)
                .build();

        repository.findByUsername(username).ifPresentOrElse(
                existing -> {
                    existing.setEndpoint(endpoint);
                    existing.setAuth(auth);
                    existing.setP256dh(p256dh);
                    repository.save(existing);
                },
                () -> repository.save(subscription)
        );

        System.out.println(" Подписка сохранена для пользователя: " + username);
        Authentication autho = SecurityContextHolder.getContext().getAuthentication();
        System.out.println(" Principal: " + autho.getPrincipal());
        System.out.println(" Authorities: " + autho.getAuthorities());
        System.out.println(" Authenticated: " + autho.isAuthenticated());

        return ResponseEntity.ok().build();
    }
}