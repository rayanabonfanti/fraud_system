package com.fraud.system.services;

import com.fraud.system.domain.user.User;
import com.fraud.system.domain.user.UserRole;
import com.fraud.system.exceptions.CustomException;
import com.fraud.system.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.security.core.userdetails.UserDetails;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class AuthorizationServiceTest {
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AuthorizationService authorizationService;

    @Test
    void loadUserByUsername_UserFound_ReturnsUserDetails() {
        String username = "testUser";
        String password = "testPassword";
        UserRole role = UserRole.USER;

        User mockUser = new User(username, password, role);
        userRepository = mock(UserRepository.class);
        when(userRepository.findByLogin(username)).thenReturn(mockUser);

        authorizationService = new AuthorizationService(userRepository);
        UserDetails userDetails = authorizationService.loadUserByUsername(username);

        assertNotNull(userDetails);
        assertEquals(username, userDetails.getUsername());
        assertEquals(password, userDetails.getPassword());
        verify(userRepository, times(1)).findByLogin(username);
    }


    @Test
    void loadUserByUsername_UserNotFound_ThrowsException() {
        String username = "nonexistentUser";
        userRepository = mock(UserRepository.class);

        when(userRepository.findByLogin(username)).thenReturn(null);

        authorizationService = new AuthorizationService(userRepository);
        CustomException exception = assertThrows(CustomException.class,
                () -> authorizationService.loadUserByUsername(username));

        assertEquals("User not found with username: " + username, exception.getMessage());
        assertEquals(400, exception.getErrorCode());

        verify(userRepository, times(1)).findByLogin(username);
    }
}
