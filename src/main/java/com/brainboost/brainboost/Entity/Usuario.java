package com.brainboost.brainboost.Entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table
public class Usuario implements UserDetails{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(name = "login")
    private String login = "Não informado.";

    @NotBlank
    @Column(name = "senha")
    private String senha;

    @Column(name = "ativo")
    private boolean ativo = false;

    @Column(name = "tokenRecSenha")
    private String tokenRecSenha;

    @Column(name = "tokenConfirmacao")
    private String tokenConfirmacao;

    @Column(name = "tokenRecSenhaValidade")
    private LocalDateTime tokenRecSenhaValidade;

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Baralho> baralhos = new HashSet<>();

    public Usuario(String email, String senha) {
        this.login = email;
        this.senha = senha;
    }

    public Usuario(String login, String senha, String tokenConfirmacao) {
        this.login = login;
        this.senha = senha;
        this.tokenConfirmacao = tokenConfirmacao;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.emptyList();
    }

    @Override
    public String getPassword() {
        return senha;
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
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return ativo;
    }
}
