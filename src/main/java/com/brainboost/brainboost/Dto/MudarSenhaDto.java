package com.brainboost.brainboost.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MudarSenhaDto {

    private String currentPassword;
    private String newPassword;

}
