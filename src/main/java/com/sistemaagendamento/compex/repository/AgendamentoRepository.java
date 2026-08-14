package com.sistemaagendamento.compex.repository;
import com.sistemaagendamento.compex.model.Agendamento;
import com.sistemaagendamento.compex.model.Status;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AgendamentoRepository extends JpaRepository<Agendamento,Long> {

    List<Agendamento> findByHorariosIdAndStatus(Long horariosId, Status status);

}
