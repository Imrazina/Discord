package dreamteam.com.chatplatform.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/ws/**").permitAll()
                        .anyRequest().permitAll()
                )
                .httpBasic(basic -> basic.disable()) // Убираем базовую авторизацию
                .headers(headers -> headers.frameOptions(frame -> frame.sameOrigin())) // Разрешаем WebSocket
                .csrf(csrf -> csrf.disable());
        // Отключаем CSRF для WebSocket
        return http.build();
    }
}