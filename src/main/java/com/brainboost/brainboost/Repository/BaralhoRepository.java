package com.brainboost.brainboost.Repository;

import com.brainboost.brainboost.Entity.Baralho;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BaralhoRepository extends JpaRepository<Baralho, Long> {

    List<Baralho> findByUsuarioId(Long usuarioId);

}
