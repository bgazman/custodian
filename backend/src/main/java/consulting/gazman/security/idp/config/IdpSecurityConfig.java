package consulting.gazman.security.idp.config;

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

@Configuration
@Order(1)
public class IdpSecurityConfig {
    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;
    private final LoggingFilter loggingFilter;

    public IdpSecurityConfig(UserDetailsService userDetailsService,
                             PasswordEncoder passwordEncoder,
                             LoggingFilter loggingFilter) {
        this.userDetailsService = userDetailsService;
        this.passwordEncoder = passwordEncoder;
        this.loggingFilter = loggingFilter;
    }

    @Bean
    public SecurityFilterChain idpSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .securityMatcher("/auth/**", "/oauth/**", "/.well-known/**", "/client/register", "/mfa/**", "/forgot-password/**")
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/**", "/oauth/**", "/.well-known/**", "/client/register", "/mfa/**", "/forgot-password/**").permitAll()
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/login")
                        .defaultSuccessUrl("/oauth/authorize", false)
                )
                .csrf(csrf -> csrf
                        .ignoringRequestMatchers("/auth/**", "/oauth/**", "/mfa/**", "/forgot-password/**")
                )
                .addFilterBefore(loggingFilter, UsernamePasswordAuthenticationFilter.class);

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