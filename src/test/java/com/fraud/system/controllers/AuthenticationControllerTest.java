package com.fraud.system.controllers;

import com.fraud.system.domain.user.AuthenticationDTO;
import com.fraud.system.domain.user.LoginResponseDTO;
import com.fraud.system.domain.user.User;
import com.fraud.system.security.TokenService;
import com.fraud.system.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class AuthenticationControllerTest {

    @InjectMocks
    private AuthenticationController authenticationController;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TokenService tokenService;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void healthcheck() {
        ResponseEntity<String> response = authenticationController.healthcheck();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("OK", response.getBody());
    }

    @Test
    void login_ValidCredentials_Success() {
        AuthenticationDTO authenticationDTO = new AuthenticationDTO("validUser", "validPassword");
        User user = new User();
        user.setBlocked(false);

        when(userRepository.findByLogin(eq("validUser"))).thenReturn(user);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(mock(Authentication.class));
        when(tokenService.generateToken(any(User.class))).thenReturn("mockedToken");

        ResponseEntity<LoginResponseDTO> response = authenticationController.login(authenticationDTO);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(new LoginResponseDTO(null), response.getBody());
        verify(userRepository).save(eq(user));
    }

    @Test
    void login_UserBlocked_Returns403() {
        AuthenticationDTO authenticationDTO = new AuthenticationDTO("blockedUser", "validPassword");
        User user = new User();
        user.setBlocked(true);

        when(userRepository.findByLogin(eq("blockedUser"))).thenReturn(user);

        ResponseEntity<String> response = authenticationController.login(authenticationDTO);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertEquals("User is blocked", response.getBody());
    }

}
