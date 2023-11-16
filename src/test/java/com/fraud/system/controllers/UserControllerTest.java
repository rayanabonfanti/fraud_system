package com.fraud.system.controllers;

import com.fraud.system.domain.user.RegisterDTO;
import com.fraud.system.domain.user.User;
import com.fraud.system.domain.user.UserRole;
import com.fraud.system.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserControllerTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserController userController;

    @Test
    public void healthcheck_ReturnsOk() {
        ResponseEntity response = userController.healthcheck();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("OK", response.getBody());
    }

    @Test
    public void register_NewUser_ReturnsOk() {
        RegisterDTO registerDTO = new RegisterDTO("newuser", "password", UserRole.USER);
        when(userRepository.findByLogin(registerDTO.login())).thenReturn(null);

        ResponseEntity response = userController.register(registerDTO);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    public void register_ExistingUser_ReturnsBadRequest() {
        RegisterDTO registerDTO = new RegisterDTO("existinguser", "password", UserRole.USER);
        when(userRepository.findByLogin(registerDTO.login())).thenReturn(new User());

        ResponseEntity response = userController.register(registerDTO);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verify(userRepository, never()).save(any(User.class));
    }
}
