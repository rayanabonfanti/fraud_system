package com.fraud.system.controllers;

import com.fraud.system.domain.user.AuthenticationDTO;
import com.fraud.system.domain.user.LoginResponseDTO;
import com.fraud.system.domain.user.User;
import com.fraud.system.security.TokenService;
import com.fraud.system.repositories.UserRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("auth")
public class AuthenticationController {
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository repository;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @GetMapping
    public ResponseEntity healthcheck() {
        return ResponseEntity.ok().body("OK");
    }

    @PostMapping("/login")
    public ResponseEntity login(@RequestBody @Valid AuthenticationDTO data) {
        try {
            User user = repository.findByLogin(data.login());

            if (user.isBlocked()) {
                return ResponseEntity.status(403).body("User is blocked");
            }

            var usernamePassword = new UsernamePasswordAuthenticationToken(data.login(), data.password());
            var auth = this.authenticationManager.authenticate(usernamePassword);

            user.resetFailedLoginAttempts();
            repository.save(user);

            var token = tokenService.generateToken((User) auth.getPrincipal());

            return ResponseEntity.ok(new LoginResponseDTO(token));
        } catch (AuthenticationException e) {
            User user = repository.findByLogin(data.login());
            user.incrementFailedLoginAttempts();
            repository.save(user);

            if (user.getFailedLoginAttempts() >= 3) {
                user.setBlocked(true);
                repository.save(user);
            }

            return ResponseEntity.status(401).body("Invalid credentials");
        }
    }

}
