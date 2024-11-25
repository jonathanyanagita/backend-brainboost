package com.brainboost.brainboost.Service;

import com.brainboost.brainboost.Dao.FlashcardDao;
import com.brainboost.brainboost.Entity.Flashcard;
import com.brainboost.brainboost.Repository.FlashcardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FlashcardService {

    @Autowired
    private FlashcardRepository repository;

    private static final int MAX_BOX_LEVEL = 5;

    private static final int[] REVIEW_INTERVALS = {1, 3, 7, 14, 30};

    private int getReviewInterval(int boxLevel) {
        return (boxLevel > 0 && boxLevel <= MAX_BOX_LEVEL) ? REVIEW_INTERVALS[boxLevel - 1] : 1;
    }

    public Flashcard answerFlashcard(Long id, boolean correct) {
        Flashcard flashcard = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Flashcard não encontrado!"));

        if (correct) {
            if (flashcard.getBoxLevel() < MAX_BOX_LEVEL) {
                flashcard.setBoxLevel(flashcard.getBoxLevel() + 1);
            }
        } else {
            flashcard.setBoxLevel(1);
        }

        // Update last review date to today
        flashcard.setLastReviewDate(LocalDate.now());

        // Calculate and set the next review date based on the updated box level
        int interval = getReviewInterval(flashcard.getBoxLevel());
        flashcard.setNextReviewDate(LocalDate.now().plus(interval, ChronoUnit.DAYS));

        return repository.save(flashcard);
    }

    public List<FlashcardDao> getFlashcardsForReview(Long baralhoId) {
        List<Flashcard> flashcards = repository.findByBaralhoId(baralhoId);
        LocalDate today = LocalDate.now();

        return flashcards.stream()
                .filter(flashcard -> {
                    // Calculate next review date based on missed reviews
                    while (today.isAfter(flashcard.getNextReviewDate())) {
                        int interval = getReviewInterval(flashcard.getBoxLevel());
                        flashcard.setNextReviewDate(flashcard.getNextReviewDate().plusDays(interval));
                    }

                    // Only return flashcards due for review today
                    return today.isEqual(flashcard.getNextReviewDate());
                })
                .map(flashcard -> new FlashcardDao(
                        flashcard.getId(),
                        flashcard.getFrente(),
                        flashcard.getVerso(),
                        flashcard.getBoxLevel(),
                        flashcard.getBaralho().getId()))
                .collect(Collectors.toList());
    }

    public long countFlashcardsForReview(Long baralhoId) {
        List<Flashcard> flashcards = repository.findByBaralhoId(baralhoId);
        LocalDate today = LocalDate.now();

        return flashcards.stream()
                .filter(flashcard -> {
                    // Calculate next review date based on missed reviews
                    LocalDate nextReviewDate = flashcard.getNextReviewDate();

                    // Calculate all missed reviews
                    while (today.isAfter(nextReviewDate)) {
                        int interval = getReviewInterval(flashcard.getBoxLevel());
                        nextReviewDate = nextReviewDate.plusDays(interval);
                    }

                    // Only include flashcards due for review today
                    return today.isEqual(nextReviewDate);
                })
                .count();
    }


    public long getFlashcardCountByDeckId(Long baralhoId) {
        return repository.countByBaralhoId(baralhoId);
    }

}
