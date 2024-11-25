package com.brainboost.brainboost.Dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CadastroDto {

    @NotBlank(message = "Digite seu e-mail")
    @Email(message = "Email inválido. Exemplo de e-mail válido: usuario@dominio.com")
    private String login;

    @NotBlank(message = "Insira sua senha.")
    @Size(min = 6, message = "Senha deve conter pelo menos 6 caracteres.")
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-zA-Z]).*$", message = "Senha deve conter letras e números.")
    private String senha;

}