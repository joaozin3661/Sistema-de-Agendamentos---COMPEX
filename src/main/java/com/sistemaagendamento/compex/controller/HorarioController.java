package com.sistemaagendamento.compex.controller;

import com.sistemaagendamento.compex.model.Horarios;
import com.sistemaagendamento.compex.service.HorarioService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/horarios")
@CrossOrigin("*")
public class HorarioController {


    private final HorarioService horarioService;
    public HorarioController(HorarioService h){
        horarioService=h;
    }

    @GetMapping
    public ResponseEntity<List<Horarios>> listarHorarios() {return ResponseEntity.ok(horarioService.listarHorarios());
    }
    @PostMapping
    public ResponseEntity<Horarios> incluirHorarios(@Valid @RequestBody Horarios horarios) {
        Horarios novo = horarioService.criarHorarios(horarios);
        return ResponseEntity.status(HttpStatus.CREATED).body(novo);
    }
    @PutMapping("/{id}")
    public ResponseEntity<Horarios> editarHorarios(@PathVariable Long id, @Valid @RequestBody Horarios horarios) {
        Horarios novo = horarioService.editarHorario(id, horarios);
        return ResponseEntity.ok(novo);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarHorario(@PathVariable Long id) {
        horarioService.deletarHorario(id);
        return ResponseEntity.noContent().build();
    }
}
