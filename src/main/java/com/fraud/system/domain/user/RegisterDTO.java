package com.fraud.system.domain.user;

public record RegisterDTO(String login, String password, UserRole role) {
}
