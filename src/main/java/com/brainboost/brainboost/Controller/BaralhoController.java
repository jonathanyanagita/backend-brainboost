package com.brainboost.brainboost.Controller;

import com.brainboost.brainboost.Dto.BaralhoDto;
import com.brainboost.brainboost.Entity.Baralho;
import com.brainboost.brainboost.Entity.Usuario;
import com.brainboost.brainboost.Repository.BaralhoRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/baralhos")
public class BaralhoController {

    @Autowired
    private BaralhoRepository repository;

    @PostMapping
    public ResponseEntity<?> adicionarBaralho(@RequestBody @Valid BaralhoDto baralhoDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        Baralho novoBaralho = new Baralho(baralhoDto.getId(), baralhoDto.getTitulo(), baralhoDto.getDescricao());
        novoBaralho.setUsuario((Usuario) userDetails);
        repository.save(novoBaralho);
        return ResponseEntity.status(HttpStatus.CREATED).body("Baralho criado com sucesso!");
    }

    @GetMapping
    public ResponseEntity<List<Baralho>> getTodosBaralhos() {
        List<Baralho> baralhos = repository.findAll();
        return ResponseEntity.ok(baralhos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Baralho> getBaralhoPorUsuarioId(@PathVariable Long id) {
        return repository.findById(id)
                .map(baralho -> ResponseEntity.ok(baralho))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<Baralho>> getBaralhosPorUsuarioId(@PathVariable Long usuarioId) {
        List<Baralho> baralhos = repository.findByUsuarioId(usuarioId);

        if (baralhos.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        return ResponseEntity.ok(baralhos);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deletarBaralho(@PathVariable Long id) {
        return repository.findById(id)
                .map(baralho -> {
                    repository.delete(baralho);
                    return ResponseEntity.noContent().build();
                })
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> atualizarBaralho(@PathVariable Long id, @RequestBody @Valid BaralhoDto baralhoDto) {
        return repository.findById(id)
                .map(baralho -> {
                    if (baralhoDto.getTitulo() != null && !baralhoDto.getTitulo().equals(baralho.getTitulo())) {
                        baralho.setTitulo(baralhoDto.getTitulo());
                    }
                    if (baralhoDto.getDescricao() != null && !baralhoDto.getDescricao().equals(baralho.getDescricao())) {
                        baralho.setDescricao(baralhoDto.getDescricao());
                    }
                    repository.save(baralho);
                    return ResponseEntity.ok("Baralho atualizado com sucesso!");
                })
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }
}
