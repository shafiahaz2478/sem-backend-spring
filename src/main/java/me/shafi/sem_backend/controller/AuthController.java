package me.shafi.sem_backend.controller;

import me.shafi.sem_backend.models.User;
import me.shafi.sem_backend.service.AuthService;
import me.shafi.sem_backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authenticationService;

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> payload) throws Exception {
        String username = payload.get("username");
        String password = payload.get("password");
        return ResponseEntity.ok().body("{\"token\": \"" + authenticationService.signin(username, password) + "\"}");
    }

    @GetMapping("/users")
    public ResponseEntity<String> getUsers() {
        List<User> users = userService.getAllUsers();

        String response = "{\"users\": [" +
                users.stream()
                        .map(user -> String.format("{\"_id\": \"%s\", \"username\": \"%s\"}", user.get_id().toString(), user.getUsername()))
                        .collect(Collectors.joining(",")) +
                "]}";

        return ResponseEntity.ok(response);
    }


}
