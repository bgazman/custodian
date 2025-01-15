package consulting.gazman.security.common.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Instant;
import java.util.UUID;

@Component
public class CustomHeaderFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        // Execute the rest of the chain first
        filterChain.doFilter(request, response);

        // Add our headers after the chain has processed
        String traceId = MDC.get("traceId");
        response.setHeader("X-Trace-Id", traceId);
        response.setHeader("X-Response-Id", UUID.randomUUID().toString());
        response.setHeader("X-Timestamp", Instant.now().toString());
    }
}