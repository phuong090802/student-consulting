package com.ute.studentconsulting.websocket;

import com.ute.studentconsulting.payload.response.ErrorResponse;
import com.ute.studentconsulting.security.token.TokenUtils;
import io.micrometer.common.lang.NonNullApi;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Objects;

@NonNullApi
@Slf4j
public class AuthSocketInterceptor implements ChannelInterceptor {
    @Autowired
    private TokenUtils tokenUtils;
    @Autowired
    private UserDetailsService userDetailsService;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        log.info("Message: {}", message);
        var accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        if (Objects.nonNull(accessor)) {
            var token = parseToken(accessor);
            if (StringUtils.hasText(token) && tokenUtils.validateToken(token)) {
                var username = tokenUtils.getUsernameFromToken(token);
                var userDetails = userDetailsService.loadUserByUsername(username);
                log.info("username: {}", userDetails.getUsername());
                var authentication = new UsernamePasswordAuthenticationToken
                        (userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(this);
                SecurityContextHolder.getContext().setAuthentication(authentication);
                accessor.setUser(authentication);
            }
        }
        return message;
    }

    private String parseToken(StompHeaderAccessor accessor) {
        var headerAuth = accessor.getFirstNativeHeader("Authorization");
        return (!StringUtils.hasText(headerAuth) || !headerAuth.startsWith("Bearer "))
                ? ""
                : headerAuth.substring(7);
    }
}
