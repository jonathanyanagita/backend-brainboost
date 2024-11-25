package com.brainboost.brainboost.Dao;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FlashcardDao

{

    private Long id;

    private String frente;
    private String verso;

    private int boxLevel;
    private Long baralhoId;



}
