package com.brainboost.brainboost.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AtualizarFlashcardDto {

    private String frente;

    private String verso;

    private String frontImageUrl;

    private String backImageUrl;

}
