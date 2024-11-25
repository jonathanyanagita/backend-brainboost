package com.brainboost.brainboost.Controller;

import com.brainboost.brainboost.Entity.Usuario;
import com.brainboost.brainboost.Repository.UsuarioRepository;
import com.brainboost.brainboost.Service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/usuarios")
public class RecuperarSenhaController {

    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private EmailService emailService;

    @PostMapping("/esqueci-senha/{email}")
    public ResponseEntity<?> resetPassword(@PathVariable String email) {
        Usuario user = (Usuario) usuarioRepository.findByLogin(email);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Email não encontrado");
        }

        String token = UUID.randomUUID().toString().substring(0, 6);
        user.setTokenRecSenha(token);
        user.setTokenRecSenhaValidade(LocalDateTime.now().plusHours(5));
        usuarioRepository.save(user);
        emailService.sendEmail(user.getLogin(), "Recuperar senha", "Para recuperar sua senha, acesse: http://localhost:8080/usuarios/recuperar-senha/"+ token);
        return ResponseEntity.ok("E-mail enviado.");
    }

    @PostMapping("/recuperar-senha/{token}")
    public ResponseEntity<?> confirmResetPassword(@PathVariable String token, @RequestParam("novasenha") String novasenha) {
        Usuario user = usuarioRepository.findBytokenRecSenha(token);
        if (user == null || user.getTokenRecSenhaValidade().isBefore(LocalDateTime.now())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Link inválido ou expirado");
        }

        user.setSenha(new BCryptPasswordEncoder().encode(novasenha));
        user.setTokenRecSenha(null); // Remover o token após a redefinição da senha
        user.setTokenRecSenhaValidade(null);
        usuarioRepository.save(user);
        return ResponseEntity.ok("Senha resetada com sucesso");
    }
}
