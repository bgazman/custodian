package consulting.gazman.security.idp.config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

//@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.disable()) // Disable CORS here to let our filter handle it
                .csrf(csrf -> csrf.disable()) // Optional: Disable CSRF if needed
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/**", "/oauth/**").permitAll()
                        .anyRequest().authenticated()
                );

        return http.build();
    }
}
