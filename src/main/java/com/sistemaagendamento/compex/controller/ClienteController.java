package com.sistemaagendamento.compex.controller;

import com.sistemaagendamento.compex.model.Cliente;
import com.sistemaagendamento.compex.service.ClienteService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/clientes")
@CrossOrigin("*")
public class ClienteController {


    final private ClienteService clienteService;
    public ClienteController(ClienteService c){
        clienteService=c;
    }

    @GetMapping
    public ResponseEntity<List<Cliente>> listarClientes(){
        return ResponseEntity.ok(clienteService.listarClientes());
    }
    @PostMapping
    public ResponseEntity<Cliente> incluirCliente(@Valid @RequestBody Cliente cliente) {
        Cliente novo = clienteService.criarCliente(cliente);
        return ResponseEntity.status(HttpStatus.CREATED).body(novo);
    }
    @PutMapping("/{id}")
    public ResponseEntity<Cliente> editarCliente(@PathVariable Long id, @Valid @RequestBody Cliente cli) {
        Cliente atualizado = clienteService.editarCliente(id, cli);
        return ResponseEntity.ok(atualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarCliente(@PathVariable Long id) {
        clienteService.deletarCliente(id);
        return ResponseEntity.noContent().build();
    }
}
