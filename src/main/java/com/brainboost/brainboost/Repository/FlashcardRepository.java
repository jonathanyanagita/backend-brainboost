package com.brainboost.brainboost.Repository;

import com.brainboost.brainboost.Entity.Flashcard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface FlashcardRepository extends JpaRepository<Flashcard, Long> {

    List<Flashcard> findByBaralhoId(Long baralhoId);

    @Query("SELECT f FROM Flashcard f WHERE f.baralho.id = :baralhoId AND (f.nextReviewDate < :nextReviewDate OR f.nextReviewDate = :nextReviewDate)")
    List<Flashcard> findByBaralhoIdWithNextReviewDateBeforeOrEqual(@Param("baralhoId") Long baralhoId, @Param("nextReviewDate") LocalDate nextReviewDate);

    List<Flashcard> findByBaralhoIdAndBoxLevelAndNextReviewDateLessThanEqual(Long deckId, int boxLevel, LocalDate date);

    List<Flashcard> findByBaralhoIdAndBoxLevel(Long deckId, int boxLevel);

    long countByBaralhoId(Long baralhoId);
}
