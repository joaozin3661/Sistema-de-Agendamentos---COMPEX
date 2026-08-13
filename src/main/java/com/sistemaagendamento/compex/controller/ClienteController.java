package com.sistemaagendamento.compex.controller;

import com.sistemaagendamento.compex.model.Cliente;
import com.sistemaagendamento.compex.service.ClienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/clientes")
@CrossOrigin("*")
public class ClienteController {

    @Autowired
    private ClienteService clienteService;

    @GetMapping
    public ResponseEntity<List<Cliente>> listarClientes(){
        return ResponseEntity.ok(clienteService.listarClientes());
    }
    @PostMapping
    public ResponseEntity<Cliente> incluirCliente(@RequestBody Cliente cliente) {
        Cliente novo = clienteService.criarCliente(cliente);
        return ResponseEntity.status(HttpStatus.CREATED).body(novo);
    }
    @PutMapping("/{id}")
    public ResponseEntity<Cliente> editarCliente(@PathVariable Long id, @RequestBody Cliente cli) {
        Cliente atualizado = clienteService.editarCliente(id, cli);
        return ResponseEntity.ok(atualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarCliente(@PathVariable Long id) {
        clienteService.deletarCliente(id);
        return ResponseEntity.noContent().build();
    }
}
