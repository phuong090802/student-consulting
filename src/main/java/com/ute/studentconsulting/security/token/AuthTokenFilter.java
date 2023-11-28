package com.ute.studentconsulting.security.token;

import com.ute.studentconsulting.security.service.impl.UserDetailsServiceImpl;
import io.micrometer.common.lang.NonNullApi;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@NonNullApi
@Slf4j
public class AuthTokenFilter extends OncePerRequestFilter {
    @Autowired
    private TokenUtils tokenUtils;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            var token = parseToken(request);
            if (StringUtils.hasText(token) && tokenUtils.validateToken(token)) {
                var username = tokenUtils.getUsernameFromToken(token);
                var userDetails = userDetailsService.loadUserByUsername(username);
                var authentication = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception e) {
            log.error("Không thể ủy quyền cho người dùng: {}", e.getMessage());
        }
        filterChain.doFilter(request, response);
    }

    private String parseToken(HttpServletRequest request) {
        var headerAuth = request.getHeader("Authorization");
        return (!StringUtils.hasText(headerAuth) || !headerAuth.startsWith("Bearer "))
                ? ""
                : headerAuth.substring(7);
    }
}
