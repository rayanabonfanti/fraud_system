package com.fraud.system.controllers;

import com.fraud.system.domain.user.AuthenticationDTO;
import com.fraud.system.domain.user.LoginResponseDTO;
import com.fraud.system.domain.user.RegisterDTO;
import com.fraud.system.domain.user.User;
import com.fraud.system.infra.security.TokenService;
import com.fraud.system.repositories.UserRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import com.fraud.system.domain.user.AuthenticationDTO;
import com.fraud.system.domain.user.LoginResponseDTO;
import com.fraud.system.domain.user.User;
import com.fraud.system.infra.security.TokenService;
import com.fraud.system.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    private BCryptPasswordEncoder passwordEncoder;  // Adicione um encoder para verificar a senha

    @GetMapping
    public ResponseEntity healthcheck() {
        return ResponseEntity.ok().body("OK");
    }

    @PostMapping("/login")
    public ResponseEntity login(@RequestBody @Valid AuthenticationDTO data) {
        try {
            // Busca o usuário no banco de dados
            User user = repository.findByLogin(data.login());

            // Verifica se o usuário está bloqueado
            if (user.isBlocked()) {
                return ResponseEntity.status(403).body("User is blocked");
            }

            // Autentica o usuário
            var usernamePassword = new UsernamePasswordAuthenticationToken(data.login(), data.password());
            var auth = this.authenticationManager.authenticate(usernamePassword);

            // Zera a contagem de tentativas falhas após um login bem-sucedido
            user.setFailedLoginAttempts(0);
            repository.save(user);

            // Gera o token
            var token = tokenService.generateToken((User) auth.getPrincipal());

            return ResponseEntity.ok(new LoginResponseDTO(token));
        } catch (AuthenticationException e) {
            // Se a autenticação falhar, incrementa a contagem de tentativas falhas
            User user = repository.findByLogin(data.login());
            user.incrementFailedLoginAttempts();
            repository.save(user);

            // Se a contagem atingir 3, bloqueia o usuário
            if (user.getFailedLoginAttempts() >= 3) {
                user.setBlocked(true);
                repository.save(user);
            }

            return ResponseEntity.status(401).body("Invalid credentials");
        }
    }

}
