package com.fraud.system.domain.user;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Document(collection = "transaction")
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private String login;
    private String password;
    private UserRole role;
    private int failedLoginAttempts; // Adicionado campo para rastrear tentativas falhas
    private boolean blocked; // Adicionado campo para rastrear o status de bloqueio

    public User(String login, String password, UserRole role){
        this.login = login;
        this.password = password;
        this.role = role;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if(this.role == UserRole.ADMIN) return List.of(new SimpleGrantedAuthority("ROLE_ADMIN"), new SimpleGrantedAuthority("ROLE_USER"));
        else return List.of(new SimpleGrantedAuthority("ROLE_USER"));
    }

    @Override
    public String getUsername() {
        return login;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !this.isBlocked(); // Alterado para verificar se o usuário está bloqueado
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return !this.isBlocked(); // Alterado para verificar se o usuário está bloqueado
    }

    // Método para incrementar as tentativas falhas
    public void incrementFailedLoginAttempts() {
        this.failedLoginAttempts++;
    }

    // Método para reiniciar as tentativas falhas
    public void resetFailedLoginAttempts() {
        this.failedLoginAttempts = 0;
    }

    // Método para verificar se o usuário está bloqueado
    public boolean isBlocked() {
        // Define a lógica de bloqueio, por exemplo, bloquear após 3 tentativas falhas
        return this.failedLoginAttempts >= 3 || this.blocked;
    }

    // Método para bloquear o usuário
    public void setBlocked(boolean blocked) {
        this.blocked = blocked;
    }

}
