package consulting.gazman.security.idp.config;

import consulting.gazman.security.common.config.CorsConfig;
import consulting.gazman.security.common.filter.BasicAuthFilter;
import consulting.gazman.security.common.filter.LoggingFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import static org.springframework.security.config.Customizer.withDefaults;
import java.util.List;

@Configuration
@Order(1)
public class IdpSecurityConfig {
    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;
    private final LoggingFilter loggingFilter;
    private final CorsConfigurationSource corsConfigurationSource;
    private BasicAuthFilter basicAuthFilter;
    // ✅ Inject CORS Bean
    public IdpSecurityConfig(UserDetailsService userDetailsService,
                             PasswordEncoder passwordEncoder,
                             LoggingFilter loggingFilter,
                             CorsConfigurationSource corsConfigurationSource,
                             BasicAuthFilter basicAuthFilter) {
        this.userDetailsService = userDetailsService;
        this.passwordEncoder = passwordEncoder;
        this.loggingFilter = loggingFilter;
        this.corsConfigurationSource = corsConfigurationSource;
        this.basicAuthFilter = basicAuthFilter;
    }

    @Bean
    public SecurityFilterChain idpSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .securityMatcher("/auth/**", "/oauth/**", "/mfa/**", "/forgot-password/**")
                .cors(cors -> cors.configurationSource(corsConfigurationSource))
                .csrf(csrf -> csrf.ignoringRequestMatchers("/auth/**", "/oauth/**", "/mfa/**", "/forgot-password/**"))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/oauth/token").hasAuthority("ROLE_CLIENT") // Add this
                        .requestMatchers("/auth/**", "/oauth/**", "/mfa/**", "/forgot-password/**").permitAll()
                        .anyRequest().authenticated()
                )
                .addFilterBefore(basicAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }




    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
