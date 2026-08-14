package com.sistemaagendamento.compex.controller;

import com.sistemaagendamento.compex.model.Agendamento;
import com.sistemaagendamento.compex.service.AgendamentoService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/agendamentos")
@CrossOrigin("*")
public class AgendamentoController {

    private final AgendamentoService agendamentoService;

    public AgendamentoController(AgendamentoService a) {
        agendamentoService = a;
    }

    @GetMapping
    public ResponseEntity<List<Agendamento>> listarAtivos() {
        return ResponseEntity.ok(agendamentoService.listarAtivos());
    }

    @PostMapping
    public ResponseEntity<Agendamento> criarAgendamento(@Valid @RequestBody AgendamentoRequest request) {
        Agendamento novo = agendamentoService.criarAgendamento(request.clienteId(), request.horarioId());
        return ResponseEntity.status(HttpStatus.CREATED).body(novo);
    }

    @PutMapping("/{id}/cancelar")
    public ResponseEntity<Agendamento> cancelarAgendamento(@PathVariable Long id) {
        return ResponseEntity.ok(agendamentoService.cancelarAgendamento(id));
    }

    public record AgendamentoRequest(@NotNull Long clienteId, @NotNull Long horarioId) {}
}
