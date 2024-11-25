package com.brainboost.brainboost.Dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BaralhoDto {

    private Long id;

    @NotBlank
    private String titulo;

    private String descricao;

    private String usuarioId;
}
