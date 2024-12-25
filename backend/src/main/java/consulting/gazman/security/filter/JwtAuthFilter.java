package consulting.gazman.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import consulting.gazman.common.dto.ApiError;
import consulting.gazman.common.dto.ApiResponse;
import consulting.gazman.security.entity.User;
import consulting.gazman.security.exception.AppException;

import consulting.gazman.security.service.impl.AuthServiceImpl;
import consulting.gazman.security.service.impl.JwtServiceImpl;
import consulting.gazman.security.utils.JwtUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final AuthServiceImpl authService;
    private final JwtServiceImpl jwtService;
    private final ObjectMapper objectMapper;
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
            String jwt = extractJwtFromRequest(request);
            if (jwt != null ) {
                String jwtValidation =  jwtService.validateToken(jwt);
                if(!Objects.equals(jwtValidation, "success")){

                    sendErrorResponse(response, jwtValidation);
                    return;
                }
                // Token exists but is invalid
                authenticateUser(jwt, request);

            }
        filterChain.doFilter(request, response);

    }

    private String extractJwtFromRequest(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }

    private void authenticateUser(String jwt, HttpServletRequest request) {
        String userEmail = JwtUtils.extractSubject(jwt);
        User user = authService.findByEmail(userEmail);
        if (user == null) {
            throw AppException.userNotFound("User not found for the given token");
        }

        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                user, null, user.getAuthorities());
        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authToken);
    }

    private void sendErrorResponse(HttpServletResponse response, String errorMessage) throws IOException {
        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.getWriter().write(objectMapper.writeValueAsString(
                ApiResponse.error("unauthorized", "Authentication failed", ApiError.of("UNAUTHORIZETTT", errorMessage))
        ));
    }

}
