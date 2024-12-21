package consulting.gazman.security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
public class CorsConfig {

    private final Environment environment;

    public CorsConfig(Environment environment) {
        this.environment = environment;
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // Load properties from application.properties
        String[] allowedOrigins = environment.getProperty("cors.allowed.origins", "").split(",");
        String[] allowedMethods = environment.getProperty("cors.allowed.methods", "").split(",");
        String[] allowedHeaders = environment.getProperty("cors.allowed.headers", "").split(",");
        String[] exposedHeaders = environment.getProperty("cors.exposed.headers", "").split(",");

        configuration.setAllowedOrigins(Arrays.asList(allowedOrigins));
        configuration.setAllowedMethods(Arrays.asList(allowedMethods));
        configuration.setAllowedHeaders(Arrays.asList(allowedHeaders));
        configuration.setExposedHeaders(Arrays.asList(exposedHeaders)); // Add this line
        configuration.setAllowCredentials(true); // Enable if needed

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
