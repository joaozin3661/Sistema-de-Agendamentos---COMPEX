package com.sistemaagendamento.compex.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.sistemaagendamento.compex.model.Horarios;

import java.time.LocalDateTime;
import java.util.List;

public interface HorariosRepository extends JpaRepository<Horarios, Long> {

    List<Horarios> findByDisponivel(Boolean disponivel);
    boolean existsByHorarioLivre(LocalDateTime horarioLivre);
}
