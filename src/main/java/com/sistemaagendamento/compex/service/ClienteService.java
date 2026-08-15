package com.sistemaagendamento.compex.service;

import com.sistemaagendamento.compex.model.Cliente;
import com.sistemaagendamento.compex.repository.ClienteRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
public class ClienteService {
    private ClienteRepository repository;

    public ClienteService(ClienteRepository repository){this.repository = repository;}

    public List<Cliente> listarClientes(){
        List<Cliente> lista = repository.findAll();
        return lista;
    }

    public Cliente criarCliente(Cliente cliente){
        if (repository.existsByCpf(cliente.getCpf())){
            throw new ResponseStatusException(HttpStatus.CONFLICT, "CPF já cadastrado");
        }

        Cliente clienteNovo = repository.save(cliente);
        return clienteNovo;
    }

    public Cliente buscarPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente não encontrado"));
    }

    public Cliente editarCliente(Long id, Cliente clienteAtt){
        Cliente clienteExistente = buscarPorId(id);

        clienteExistente.setNome(clienteAtt.getNome());
        clienteExistente.setCpf(clienteAtt.getCpf());
        clienteExistente.setIdade(clienteAtt.getIdade());

        return repository.save(clienteExistente);
    }

    public void deletarCliente(Long id){
        Cliente cliente = buscarPorId(id);

        repository.delete(cliente);
    }
}
