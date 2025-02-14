package consulting.gazman.security.common.filter;

import consulting.gazman.security.idp.oauth.service.OAuthClientService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Base64;
import java.util.List;

@Configuration
public class BasicAuthFilter extends OncePerRequestFilter {
    private final OAuthClientService oAuthClientService;

    public BasicAuthFilter(OAuthClientService oAuthClientService) {
        this.oAuthClientService = oAuthClientService;
    }


    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {
        if (request.getRequestURI().equals("/oauth/token")) {
            String authHeader = request.getHeader("Authorization");
            AuthResult result = isValidBasicAuth(authHeader);
            if (!result.isValid()) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }

            List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_CLIENT"));
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(result.clientId(), null, authorities);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        chain.doFilter(request, response);
    }
    private record AuthResult(String clientId, boolean isValid) {}

    private AuthResult isValidBasicAuth(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Basic ")) {
            return new AuthResult(null, false);
        }

        String base64Credentials = authHeader.substring("Basic ".length());
        String credentials = new String(Base64.getDecoder().decode(base64Credentials));
        String[] values = credentials.split(":", 2);

        if (values.length != 2) {
            return new AuthResult(null, false);
        }

        String clientId = values[0];
        String clientSecret = values[1];

        return new AuthResult(clientId, validateClientCredentials(clientId, clientSecret));
    }
    private boolean validateClientCredentials(String clientId, String clientSecret) {
        return oAuthClientService.validateClientSecret(clientId, clientSecret);
    }
}