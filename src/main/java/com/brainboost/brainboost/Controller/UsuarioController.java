package com.brainboost.brainboost.Controller;

import com.brainboost.brainboost.Dto.*;
import com.brainboost.brainboost.Entity.Usuario;
import com.brainboost.brainboost.Repository.UsuarioRepository;
import com.brainboost.brainboost.Service.EmailService;
import com.brainboost.brainboost.Service.TokenService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.security.SecureRandom;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@CrossOrigin(origins = "http://localhost:8080")
@RestController
@RequestMapping("/usuarios")
public class UsuarioController{

    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private UsuarioRepository repository;
    @Autowired
    private TokenService tokenService;
    @Autowired
    private EmailService emailService;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity logar(@RequestBody @Valid AuthenticationDto data){
        var usernamePassword = new UsernamePasswordAuthenticationToken(data.login(), data.senha());
        var auth = this.authenticationManager.authenticate(usernamePassword);
        Usuario usuario = (Usuario) auth.getPrincipal();
        var token = tokenService.generateToken((Usuario) auth.getPrincipal());
        return ResponseEntity.ok(new LoginResponseDto(token, usuario.getId()));
    }

    @PostMapping("/cadastro")
    public ResponseEntity<?> cadastrar(@RequestBody @Valid CadastroDto data, BindingResult result) {
        if (result.hasErrors()) {
            List<String> errors = result.getAllErrors().stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .collect(Collectors.toList());

            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "errors", errors
            ));
        }

        if (this.repository.findByLogin(data.getLogin()) != null) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Usuário já cadastrado."
            ));
        }

        String encryptedPassword = new BCryptPasswordEncoder().encode(data.getSenha());
        Usuario novoUsuario = new Usuario(data.getLogin(), encryptedPassword);
        this.repository.save(novoUsuario);

        SecureRandom random = new SecureRandom();
        String token = String.format("%06d", random.nextInt(1000000));
        novoUsuario.setTokenConfirmacao(token);
        repository.save(novoUsuario);

        try {
            emailService.sendEmail(novoUsuario.getLogin(), "Favor confirmar e-mail",
                    "Utilize o token a seguir para habilitar sua conta: " + token);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "success", false,
                    "message", "Erro ao enviar o e-mail de confirmação."
            ));
        }
        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "E-mail enviado."
        ));
    }

    @GetMapping("/email/{email}")
    public ResponseEntity buscarPorEmail(@PathVariable String email){
        Usuario usuario = (Usuario) repository.findByLogin(email);
        if (usuario != null) {
            return ResponseEntity.ok(email);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/cadastro/reenviar")
    public ResponseEntity reenviarEmailConfirmacao(@RequestParam String email){
        Usuario user = (Usuario) repository.findByLogin(email);
        String token = user.getTokenConfirmacao();
        if (token==null){
            token = UUID.randomUUID().toString();
            user.setTokenConfirmacao(token);
            repository.save(user);
        } else
        emailService.sendEmail(user.getLogin(), "Favor confirmar e-mail", "Utilize o token a seguir para habilitar sua conta:"+ token);
        return ResponseEntity.ok("Email enviado.");
    }

    @PutMapping("/cadastro/confirmar")
    public ResponseEntity<?> confirmarEmail(@RequestParam String token){
        Usuario user = repository.findBytokenConfirmacao(token);

        user.setAtivo(true);
        user.setTokenConfirmacao(null); // Remover o token após a redefinição da senha
        repository.save(user);
        return ResponseEntity.ok("E-mail confirmado com sucesso!");
    }

    @GetMapping("/id/{id}")
    public ResponseEntity buscarUsuario(@PathVariable Long id){
        Optional<Usuario> optionalUsuario = repository.findById(id);
        if (optionalUsuario.isPresent()) {
            Usuario usuario = optionalUsuario.get();
            UsuarioDto usuarioDto = new UsuarioDto(usuario.getId(), usuario.getLogin());
            return ResponseEntity.ok(usuarioDto);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletarUsuario(@PathVariable Long id) {
        Optional<Usuario> optionalUsuario = repository.findById(id);

        if (optionalUsuario.isPresent()) {
            Usuario usuario = optionalUsuario.get();

            repository.deleteById(id);

            emailService.sendEmail(usuario.getLogin(), "Brainboost - Exclusão de conta", "A sua conta no Brainboost foi excluida com sucesso.");

            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } else {
            // User not found
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/senha")
    public ResponseEntity<?> alterarSenha(@RequestBody @Valid MudarSenhaDto mudarSenhaDto, Authentication authentication) {
        Usuario usuario = (Usuario) authentication.getPrincipal();

        if (!passwordEncoder.matches(mudarSenhaDto.getCurrentPassword(), usuario.getSenha())) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "Senha atual incorreta"));
        }

        String newEncryptedPassword = passwordEncoder.encode(mudarSenhaDto.getNewPassword());
        usuario.setSenha(newEncryptedPassword);
        repository.save(usuario);

        return ResponseEntity.ok(Map.of("success", true, "message", "Senha atualizada com sucesso."));
    }
}