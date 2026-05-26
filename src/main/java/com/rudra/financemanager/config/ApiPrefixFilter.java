package com.rudra.financemanager.config;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Filter that transparently intercepts requests lacking the mandatory "/api" prefix
 * and dynamically forwards them to the appropriate API handler under the hood.
 * This guarantees seamless backward compatibility with client test scripts or integrations
 * that invoke endpoints without specifying "/api".
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class ApiPrefixFilter implements Filter {

    @Override
    public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain)
            throws ServletException, IOException {
        final HttpServletRequest httpRequest = (HttpServletRequest) request;
        final String path = httpRequest.getRequestURI();

        if (!path.startsWith("/api") && !path.startsWith("/h2-console") && !path.equals("/error")) {
            if (path.startsWith("/auth") || path.startsWith("/transactions")
                    || path.startsWith("/categories") || path.startsWith("/goals") || path.startsWith("/reports")) {

                final String newPath = "/api" + path;
                request.getRequestDispatcher(newPath).forward(request, response);
                return;
            }
        }
        chain.doFilter(request, response);
    }
}
