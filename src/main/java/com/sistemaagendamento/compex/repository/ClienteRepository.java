package com.sistemaagendamento.compex.repository;

import com.sistemaagendamento.compex.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ICliente extends JpaRepository <Cliente, Long>{
}
