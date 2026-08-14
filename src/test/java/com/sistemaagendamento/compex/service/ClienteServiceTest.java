package com.sistemaagendamento.compex.service;

import com.sistemaagendamento.compex.model.Cliente;
import com.sistemaagendamento.compex.repository.ClienteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClienteServiceTest {

    @Mock
    private ClienteRepository repository;

    private ClienteService clienteService;

    @BeforeEach
    void setUp() {
        clienteService = new ClienteService(repository);
    }

    private Cliente criarCliente(Long id, String nome, String cpf, int idade) {
        Cliente cliente = new Cliente();
        cliente.setNome(nome);
        cliente.setCpf(cpf);
        cliente.setIdade(idade);
        return cliente;
    }

    @Test
    void listarClientes_TodosOsClientes() {
        List<Cliente> clientes = List.of(
                criarCliente(1L, "Maria", "11111111111", 30),
                criarCliente(2L, "João", "22222222222", 25)
        );
        when(repository.findAll()).thenReturn(clientes);

        List<Cliente> resultado = clienteService.listarClientes();

        assertThat(resultado).hasSize(2).containsExactlyElementsOf(clientes);
        verify(repository).findAll();
    }

    @Test
    void criarCliente_CriarESalvarCliente() {
        Cliente cliente = criarCliente(null, "Maria", "11111111111", 30);
        Cliente clienteSalvo = criarCliente(1L, "Maria", "11111111111", 30);
        when(repository.save(cliente)).thenReturn(clienteSalvo);

        Cliente resultado = clienteService.criarCliente(cliente);

        assertThat(resultado).isEqualTo(clienteSalvo);
        verify(repository).save(cliente);
    }

    @Test
    void buscarPorId_ClienteExistente() {
        Cliente cliente = criarCliente(1L, "Maria", "11111111111", 30);
        when(repository.findById(1L)).thenReturn(Optional.of(cliente));

        Cliente resultado = clienteService.buscarPorId(1L);

        assertThat(resultado).isEqualTo(cliente);
    }

    @Test
    void buscarPorId_ClienteNaoExiste_Exception() {
        when(repository.findById(2L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> clienteService.buscarPorId(2L))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Cliente não encontrado");
    }

    @Test
    void editarCliente_AtualizarESalvar() {
        Cliente existente = criarCliente(1L, "Maria", "11111111111", 30);
        Cliente atualizacao = criarCliente(null, "Maria Silva", "22222222222", 31);
        when(repository.findById(1L)).thenReturn(Optional.of(existente));
        when(repository.save(any(Cliente.class))).thenAnswer(inv -> inv.getArgument(0));

        Cliente resultado = clienteService.editarCliente(1L, atualizacao);

        assertThat(resultado.getNome()).isEqualTo("Maria Silva");
        assertThat(resultado.getCpf()).isEqualTo("22222222222");
        assertThat(resultado.getIdade()).isEqualTo(31);
        verify(repository).save(existente);
    }

    @Test
    void editarCliente_ClienteNaoExiste_Exception() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> clienteService.editarCliente(99L, criarCliente(null, "X", "0", 1)))
                .isInstanceOf(ResponseStatusException.class);

        verify(repository, never()).save(any());
    }

    @Test
    void deletarCliente_ClienteExiste_Delete() {
        Cliente cliente = criarCliente(1L, "Maria", "11111111111", 30);
        when(repository.findById(1L)).thenReturn(Optional.of(cliente));

        clienteService.deletarCliente(1L);

        verify(repository).delete(cliente);
    }

    @Test
    void deletarCliente_ClienteNaoExiste_Exception() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> clienteService.deletarCliente(99L))
                .isInstanceOf(ResponseStatusException.class);

        verify(repository, never()).delete(any());
    }
}
