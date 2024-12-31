package consulting.gazman.security.config;


import com.fasterxml.jackson.databind.ObjectMapper;
import consulting.gazman.security.filter.JwtExceptionFilter;
import jakarta.servlet.DispatcherType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.server.resource.web.authentication.BearerTokenAuthenticationFilter;
import org.springframework.security.web.SecurityFilterChain;

import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.web.cors.CorsConfigurationSource;

@EnableWebSecurity
@Configuration
public class SecurityConfig {

    private final PasswordEncoder passwordEncoder;
    private final UserDetailsService userDetailsService;
    private final CorsConfigurationSource corsConfigurationSource;
    private final ObjectMapper objectMapper;
    public SecurityConfig(PasswordEncoder passwordEncoder,
                          UserDetailsService userDetailsService,
                          CorsConfigurationSource corsConfigurationSource,
                          ObjectMapper objectMapper
    ) {
        this.passwordEncoder = passwordEncoder;
        this.userDetailsService = userDetailsService;
        this.corsConfigurationSource = corsConfigurationSource;

        this.objectMapper = objectMapper;
    }
    // IDP SERVER CHAIN
    @Bean
    @Order(1)
    public SecurityFilterChain oauthSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .securityMatcher("/oauth/**", "/login", "/.well-known/**", "/client/register") // Matches relevant paths
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/login").permitAll() // Public login page
                        .requestMatchers("/oauth/token").permitAll() // Public token endpoint
                        .requestMatchers("/oauth/authorize").permitAll() // Public for initiating OAuth
                        .requestMatchers("/oauth/login").permitAll() // Public for initiating OAuth
                        .requestMatchers("/.well-known/**").permitAll() // Public JWKS and metadata
                        .requestMatchers("/client/register").permitAll() // Public client registration
                        .anyRequest().authenticated() // All other requests require authentication
                )
                .formLogin(form -> form
                        .loginPage("/login") // Custom login page
                        .loginProcessingUrl("/login") // Login form submission URL
                        .defaultSuccessUrl("/oauth/authorize", false) // Redirect here after successful login
                )
                .csrf(csrf -> csrf.ignoringRequestMatchers("/oauth/**")); // Disable CSRF for OAuth endpoints
        return http.build();
    }


// Client Backend Chain
@Bean
@Order(2)
public SecurityFilterChain apiSecurityFilterChain(HttpSecurity http) throws Exception {
    http
            .securityMatcher("/api/**")
            .csrf(AbstractHttpConfigurer::disable)
            .cors(cors -> cors.configurationSource(corsConfigurationSource))
            .authorizeHttpRequests(auth -> auth
                    .requestMatchers("/api/public/**").permitAll()
                    .requestMatchers("/api/secure/**").authenticated()
                    .anyRequest().authenticated()
            )
            .oauth2ResourceServer(oauth2 -> oauth2
                    .jwt(Customizer.withDefaults())
            );
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
