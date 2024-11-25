package com.brainboost.brainboost.Dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FlashcardDto {

    private Long id;

    @NotBlank
    private String frente;

    @NotBlank
    private String verso;

    private String front_image = null;

    private String back_image = null;

    private Integer level = 0;

    private LocalDate data = LocalDate.now();

    private Long baralho_id;
}
