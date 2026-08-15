package com.sistemaagendamento.compex.repository;

import com.sistemaagendamento.compex.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClienteRepository extends JpaRepository <Cliente, Long>{

    boolean existsByCpf(String cpf);
}
