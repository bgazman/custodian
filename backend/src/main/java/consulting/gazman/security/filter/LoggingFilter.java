package consulting.gazman.security.filter;


import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;


@Slf4j
@Component
@Order(0)

public class LoggingFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        // Generate a new correlation ID or use the provided X-B3-TraceId
        String traceId = request.getHeader("X-B3-TraceId");
        if (traceId == null || traceId.isEmpty()) {
            traceId = UUID.randomUUID().toString(); // Fallback to a generated UUID
        }

        // Put traceId in MDC for logging
        MDC.put("traceId", traceId);

        try {
            // Log the request details
            log.info("Incoming request: method={}, URI={}, traceId={}",
                    request.getMethod(),
                    request.getRequestURI(),
                    traceId);

            // Continue the filter chain
            chain.doFilter(request, response);

        } finally {
            // Clear MDC to prevent memory leaks
            MDC.clear();
        }
    }


}
