package com.ute.studentconsulting.security.service.impl;

import com.ute.studentconsulting.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UserService userService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        var user = userService.findByPhone(username);
        var authorities = Collections.singletonList(
                new SimpleGrantedAuthority(user.getRole().getName().name()));
        return new UserDetailsImpl(user.getId(),
                user.getPhone(),
                user.getPassword(),
                user.getEnabled(),
                authorities);
    }
}
