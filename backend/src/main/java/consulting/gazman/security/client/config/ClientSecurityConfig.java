package consulting.gazman.security.client.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import consulting.gazman.security.common.dto.ApiError;
import consulting.gazman.security.common.filter.LoggingFilter;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;

import java.time.Instant;
import java.util.Map;

@EnableMethodSecurity(prePostEnabled = true)
//@Configuration
@Order(2)
public class ClientSecurityConfig {
    private final CorsConfigurationSource corsConfigurationSource;
    private final LoggingFilter loggingFilter;
    private final ObjectMapper objectMapper;

    public ClientSecurityConfig(CorsConfigurationSource corsConfigurationSource,
                                LoggingFilter loggingFilter,
                                ObjectMapper objectMapper) {
        this.corsConfigurationSource = corsConfigurationSource;
        this.loggingFilter = loggingFilter;
        this.objectMapper = objectMapper;
    }

    @Bean
    public SecurityFilterChain clientSecurityFilterChain(HttpSecurity http) throws Exception {
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
                )
                .exceptionHandling(exception -> exception
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            ApiError error = ApiError.builder()
                                    .code("FORBIDDEN")
                                    .message("You don't have permission to access this resource")
                                    .details(Map.of(
                                            "path", request.getRequestURI(),
                                            "traceId", MDC.get("traceId"),
                                            "timestamp", Instant.now().toString()
                                    ))
                                    .build();

                            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                            response.getWriter().write(objectMapper.writeValueAsString(error));
                        })
                        .authenticationEntryPoint((request, response, authException) -> {
                            ApiError error = ApiError.builder()
                                    .code("UNAUTHORIZED")
                                    .message("Authentication is required to access this resource")
                                    .details(Map.of(
                                            "path", request.getRequestURI(),
                                            "traceId", MDC.get("traceId"),
                                            "timestamp", Instant.now().toString()
                                    ))
                                    .build();

                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                            response.getWriter().write(objectMapper.writeValueAsString(error));
                        })
                )
                .addFilterBefore(loggingFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}