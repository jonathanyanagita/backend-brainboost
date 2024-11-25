package com.brainboost.brainboost.Controller;

import com.brainboost.brainboost.Dao.FlashcardDao;
import com.brainboost.brainboost.Dto.AtualizarFlashcardDto;
import com.brainboost.brainboost.Dto.FlashcardDto;
import com.brainboost.brainboost.Entity.Baralho;
import com.brainboost.brainboost.Entity.Flashcard;
import com.brainboost.brainboost.Repository.BaralhoRepository;
import com.brainboost.brainboost.Repository.FlashcardRepository;
import com.brainboost.brainboost.Service.FlashcardService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/flashcards")
public class FlashcardController {

    @Autowired
    private FlashcardRepository repository;

    @Autowired
    private FlashcardService service;

    @Autowired
    private BaralhoRepository baralhoRepository;

    @PostMapping("/{baralhoId}")
    private ResponseEntity<?> adicionarFlashcard(@PathVariable Long baralhoId, @Valid @RequestBody FlashcardDto flashcardDto) {

        Baralho baralho = baralhoRepository.findById(baralhoId)
                .orElseThrow(() -> new RuntimeException("Baralho não encontrado"));

        Flashcard novoFlashcard = new Flashcard(flashcardDto.getId(), flashcardDto.getFrente(), flashcardDto.getVerso(), flashcardDto.getFront_image(), flashcardDto.getBack_image());
        novoFlashcard.setBaralho(baralho);
        repository.save(novoFlashcard);

        return ResponseEntity.status(HttpStatus.CREATED).body("Flashcard criado com sucesso!");
    }

    @GetMapping("/{baralhoId}")
    public ResponseEntity<List<FlashcardDao>> getFlashcardsByDeckId(@PathVariable Long baralhoId) {
        List<Flashcard> flashcards = repository.findByBaralhoId(baralhoId);

        if (flashcards.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        List<FlashcardDao> responseDtoList = flashcards.stream()
                .map(flashcard -> new FlashcardDao(
                        flashcard.getId(),
                        flashcard.getFrente(),
                        flashcard.getVerso(),
                        flashcard.getBoxLevel(),
                        flashcard.getBaralho().getId()))
                .collect(Collectors.toList());

        return ResponseEntity.ok(responseDtoList);
    }

    @GetMapping
    public ResponseEntity<?> ListarTodosFlashcards() {
        return ResponseEntity.ok(repository.findAll());
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> atualizarFlashcard(@PathVariable Long id, @RequestBody @Valid AtualizarFlashcardDto flashcardDto) {
        return repository.findById(id)
                .map(flashcard -> {
                    flashcard.setFrente(flashcardDto.getFrente());
                    flashcard.setVerso(flashcardDto.getVerso());
                    flashcard.setFront_image(flashcardDto.getFrontImageUrl());
                    flashcard.setBack_image(flashcardDto.getBackImageUrl());
                    repository.save(flashcard);
                    return ResponseEntity.ok("Flashcard atualizado com sucesso!");
                })
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());

    }

    @DeleteMapping("/{id}")
    public ResponseEntity deletarFlashcard(@PathVariable Long id) {
        return repository.findById(id)
                .map(flashcard -> {
                    repository.delete(flashcard);
                    return ResponseEntity.noContent().build();
                })
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    ////////////////////////////////////////////////////////////////////

    @PostMapping("/{id}/answer")
    public Flashcard answerFlashcard(@PathVariable Long id, @RequestParam boolean correct) {
        return service.answerFlashcard(id, correct);
    }

    @GetMapping("/{baralhoId}/boxlevel/{boxLevel}")
    public ResponseEntity<List<FlashcardDao>> getFlashcardsByDeckIdAndBoxLevel(@PathVariable Long baralhoId, @PathVariable int boxLevel) {
        List<Flashcard> flashcards = repository.findByBaralhoIdAndBoxLevel(baralhoId, boxLevel);

        if (flashcards.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        List<FlashcardDao> responseDtoList = flashcards.stream()
                .map(flashcard -> new FlashcardDao(flashcard.getId(), flashcard.getFrente(),
                        flashcard.getVerso(), flashcard.getBoxLevel(), flashcard.getBaralho().getId()))
                .collect(Collectors.toList());

        return ResponseEntity.ok(responseDtoList);
    }

    @GetMapping("/{baralhoId}/review")
    public ResponseEntity<List<FlashcardDao>> getFlashcardsForReview(@PathVariable Long baralhoId) {
        List<FlashcardDao> flashcardsForReview = service.getFlashcardsForReview(baralhoId);
        if (flashcardsForReview.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(flashcardsForReview);
    }

    @GetMapping("/{baralhoId}/review/count")
    public ResponseEntity<Long> getCountOfFlashcardsToReview(@PathVariable Long baralhoId) {
        long count = service.countFlashcardsForReview(baralhoId);
        return ResponseEntity.ok(count);
    }

    @GetMapping("/count/{baralhoId}")
    public ResponseEntity<Long> getFlashcardCount(@PathVariable Long baralhoId) {
        long count = service.getFlashcardCountByDeckId(baralhoId);
        return ResponseEntity.ok(count);
    }

}
