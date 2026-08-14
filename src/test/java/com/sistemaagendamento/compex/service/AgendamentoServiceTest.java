package com.sistemaagendamento.compex.service;

import com.sistemaagendamento.compex.model.Agendamento;
import com.sistemaagendamento.compex.model.Cliente;
import com.sistemaagendamento.compex.model.Horarios;
import com.sistemaagendamento.compex.model.Status;
import com.sistemaagendamento.compex.repository.AgendamentoRepository;
import com.sistemaagendamento.compex.repository.ClienteRepository;
import com.sistemaagendamento.compex.repository.HorariosRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AgendamentoServiceTest {

    @Mock
    private AgendamentoRepository agendamentoRepository;
    @Mock
    private ClienteRepository clienteRepository;
    @Mock
    private HorariosRepository horariosRepository;

    private AgendamentoService agendamentoService;

    @BeforeEach
    void setUp() {
        agendamentoService = new AgendamentoService(agendamentoRepository, clienteRepository, horariosRepository);
    }

    private Cliente criarCliente(Long id) {
        Cliente cliente = new Cliente();
        cliente.setNome("Ana");
        cliente.setCpf("11111111111");
        cliente.setIdade(30);
        return cliente;
    }

    private Horarios criarHorario(boolean disponivel) {
        Horarios horario = new Horarios();
        horario.setHorarioLivre(LocalDateTime.of(2026, 9, 9, 9, 0));
        horario.setDisponivel(disponivel);
        return horario;
    }

    @Test
    void listarAtivos_AgendamentosAtivos() {
        Agendamento ativo = new Agendamento();
        ativo.setStatus(Status.ATIVO);
        Agendamento cancelado = new Agendamento();
        cancelado.setStatus(Status.CANCELADO);
        when(agendamentoRepository.findAll()).thenReturn(List.of(ativo, cancelado));

        List<Agendamento> resultado = agendamentoService.listarAtivos();

        assertThat(resultado).containsExactly(ativo);
    }

    @Test
    void buscarPorId_AgendamentoNaoExiste_Excepton() {
        when(agendamentoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> agendamentoService.buscarPorId(99L))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Agendamento não encontrado");
    }

    @Test
    void criarAgendamento_eDaCerto() {
        Cliente cliente = criarCliente(1L);
        Horarios horario = criarHorario(true);
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
        when(horariosRepository.findById(2L)).thenReturn(Optional.of(horario));
        when(agendamentoRepository.findByHorariosIdAndStatus(2L, Status.ATIVO)).thenReturn(List.of());
        when(agendamentoRepository.save(any(Agendamento.class))).thenAnswer(inv -> inv.getArgument(0));

        Agendamento resultado = agendamentoService.criarAgendamento(1L, 2L);

        assertThat(resultado.getCliente()).isEqualTo(cliente);
        assertThat(resultado.getHorarios()).isEqualTo(horario);
        assertThat(resultado.getStatus()).isEqualTo(Status.ATIVO);
        assertThat(horario.getDisponivel()).isFalse();
        verify(horariosRepository).save(horario);
    }

    @Test
    void criarAgendamento_ClienteNaoExiste_Erro() {
        when(clienteRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> agendamentoService.criarAgendamento(1L, 2L))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Cliente não encontrado");

        verify(agendamentoRepository, never()).save(any());
    }

    @Test
    void criarAgendamento_HorarioNaoExiste_Erro() {
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(criarCliente(1L)));
        when(horariosRepository.findById(2L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> agendamentoService.criarAgendamento(1L, 2L))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Horário não encontrado");

        verify(agendamentoRepository, never()).save(any());
    }

    @Test
    void criarAgendamento_HorarioAtivoJaExisteNaLista_Conflict() {
        Cliente cliente = criarCliente(1L);
        Horarios horario = criarHorario(true);
        Agendamento existente = new Agendamento();
        existente.setStatus(Status.ATIVO);
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
        when(horariosRepository.findById(2L)).thenReturn(Optional.of(horario));
        when(agendamentoRepository.findByHorariosIdAndStatus(2L, Status.ATIVO)).thenReturn(List.of(existente));

        assertThatThrownBy(() -> agendamentoService.criarAgendamento(1L, 2L))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Horário já está ocupado");

        verify(agendamentoRepository, never()).save(any());
    }

    @Test
    void criarAgendamento_HorarioComoIndisponivelMasNaoTemAgendamentoAtivo_Conflict() {
        Cliente cliente = criarCliente(1L);
        Horarios horario = criarHorario(false);
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
        when(horariosRepository.findById(2L)).thenReturn(Optional.of(horario));
        when(agendamentoRepository.findByHorariosIdAndStatus(2L, Status.ATIVO)).thenReturn(List.of());

        assertThatThrownBy(() -> agendamentoService.criarAgendamento(1L, 2L))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Horário já está ocupado");

        verify(agendamentoRepository, never()).save(any());
    }

    @Test
    void cancelarAgendamento_TrasnformaAtivoEmCancelado() {
        Horarios horario = criarHorario(false);
        Agendamento agendamento = new Agendamento();
        agendamento.setStatus(Status.ATIVO);
        agendamento.setHorarios(horario);
        when(agendamentoRepository.findById(1L)).thenReturn(Optional.of(agendamento));
        when(agendamentoRepository.save(any(Agendamento.class))).thenAnswer(inv -> inv.getArgument(0));

        Agendamento resultado = agendamentoService.cancelarAgendamento(1L);

        assertThat(resultado.getStatus()).isEqualTo(Status.CANCELADO);
        assertThat(horario.getDisponivel()).isTrue();
        verify(horariosRepository).save(horario);
    }

    @Test
    void cancelarAgendamento_AgendamentoNaoExiste_Exception() {
        when(agendamentoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> agendamentoService.cancelarAgendamento(99L))
                .isInstanceOf(ResponseStatusException.class);

        verify(agendamentoRepository, never()).save(any());
    }

    @Test
    void cancelarAgendamento_CancelarJaCancelado_Conflict() {
        Agendamento agendamento = new Agendamento();
        agendamento.setStatus(Status.CANCELADO);
        agendamento.setHorarios(criarHorario(true));
        when(agendamentoRepository.findById(1L)).thenReturn(Optional.of(agendamento));

        assertThatThrownBy(() -> agendamentoService.cancelarAgendamento(1L))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("já está cancelado");

        verify(agendamentoRepository, never()).save(any());
    }
}
