package com.example.TEAM_B_backend.core.config;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class SameSiteCookieFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        chain.doFilter(request, response);

        if (response instanceof HttpServletResponse res) {
            for (String header : res.getHeaders("Set-Cookie")) {
                if (header.contains("JSESSIONID")) {
                    res.setHeader("Set-Cookie", header + "; SameSite=None; Secure");
                }
            }
        }
    }
}
