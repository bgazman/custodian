package consulting.gazman.security.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import consulting.gazman.common.dto.ApiError;
import consulting.gazman.common.dto.ApiResponse;
import consulting.gazman.common.filter.CustomHeaderFilter;

import consulting.gazman.security.filter.JwtAuthFilter;
import jakarta.servlet.DispatcherType;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.context.SecurityContextHolderFilter;
import org.springframework.web.cors.CorsConfigurationSource;

@EnableWebSecurity
@Configuration
public class SecurityConfig {

    private final PasswordEncoder passwordEncoder;
    private final JwtAuthFilter jwtAuthFilter;
    private final UserDetailsService userDetailsService;
    private final CorsConfigurationSource corsConfigurationSource;
    private final ObjectMapper objectMapper;

    public SecurityConfig(PasswordEncoder passwordEncoder,
                          JwtAuthFilter jwtAuthFilter,
                          UserDetailsService userDetailsService,
                          CorsConfigurationSource corsConfigurationSource,
                          ObjectMapper objectMapper) {
        this.passwordEncoder = passwordEncoder;
        this.jwtAuthFilter = jwtAuthFilter;
        this.userDetailsService = userDetailsService;
        this.corsConfigurationSource = corsConfigurationSource;
        this.objectMapper = objectMapper;  // Add this

    }
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



    @Bean
    @Order(2)
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .securityMatcher("/api/**")
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                // Disable CSRF as we're using JWT tokens
                .csrf(AbstractHttpConfigurer::disable)
                // Configure CORS using the provided configuration source
                .cors(cors -> cors.configurationSource(corsConfigurationSource))
                // Configure exception handling for various HTTP status codes
                .exceptionHandling(exceptionHandling -> exceptionHandling
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            response.setContentType("application/json");
                            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                            response.getWriter().write(objectMapper.writeValueAsString(
                                    ApiError.builder()
                                            .code("ACCESS_DENIED")
                                            .message("Access Denied")
                                            .build()
                            ));
                        }))

                // Configure authorization rules
                .authorizeHttpRequests(auth -> auth
                        .dispatcherTypeMatchers(DispatcherType.ERROR).permitAll() // Allow error dispatchers
                        .requestMatchers("/api/public/**").permitAll() // Public endpoints
                        .requestMatchers("/api/secure/**").authenticated() // Secure endpoints require authentication
                        .anyRequest().denyAll() // All other endpoints require authentication
                )
                // Configure session management to be stateless (no sessions)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                // Add the authentication provider
                .authenticationProvider(authenticationProvider())
                // Add filters in the correct order
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterAfter(new CustomHeaderFilter(), SecurityContextHolderFilter.class);

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
