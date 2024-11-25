package com.brainboost.brainboost.Entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table
public class Flashcard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(name = "frente")
    private String frente;

    @NotBlank
    @Column(name = "verso")
    private String verso;

    @Column(name = "data")
    private LocalDate localDate;

    @Column(name = "front_image")
    private String front_image;

    @Column(name = "back_image")
    private String back_image;

    @ManyToOne
    @JoinColumn(name = "baralho_id")
    private Baralho baralho;

    private int boxLevel = 1;

    private LocalDate nextReviewDate = LocalDate.now();

    private LocalDate lastReviewDate = LocalDate.now();

    public Flashcard(Long id, String frente, String verso, String front_image, String back_image){
        this.id = id;
        this.frente = frente;
        this.verso = verso;
        this.front_image = front_image;
        this.back_image = back_image;
    }
}
