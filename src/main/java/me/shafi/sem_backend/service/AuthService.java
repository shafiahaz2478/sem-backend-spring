package me.shafi.sem_backend.service;

import lombok.RequiredArgsConstructor;
import me.shafi.sem_backend.models.User;
import me.shafi.sem_backend.respository.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthService  {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public String signup(String username, String password) {
//        User user = new User(username, passwordEncoder.encode(password), List.of(new SimpleGrantedAuthority("admin")));
//
//        userRepository.save(user);
//        return jwtService.generateToken(user);
        return "TODO";
    }


    public String signin(String username, String password) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password));
        var user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Invalid username or password"));
        var jwt = jwtService.generateToken(user);
        return jwt;
    }
}
