package com.sistemaagendamento.compex.service;

import com.sistemaagendamento.compex.model.Horarios;
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
class HorarioServiceTest {

    @Mock
    private HorariosRepository horarioRepository;

    private HorarioService horarioService;

    @BeforeEach
    void setUp() {
        horarioService = new HorarioService(horarioRepository);
    }

    private Horarios criarHorario(LocalDateTime data, boolean disponivel) {
        Horarios horario = new Horarios();
        horario.setHorarioLivre(data);
        horario.setDisponivel(disponivel);
        return horario;
    }

    @Test
    void listarHorarios_TodosOsHorarios() {
        List<Horarios> horarios = List.of(
                criarHorario(LocalDateTime.of(2026, 8, 20, 9, 0), true),
                criarHorario(LocalDateTime.of(2026, 8, 20, 10, 0), false)
        );
        when(horarioRepository.findAll()).thenReturn(horarios);

        List<Horarios> resultado = horarioService.listarHorarios();

        assertThat(resultado).hasSize(2).containsExactlyElementsOf(horarios);
        verify(horarioRepository).findAll();
    }

    @Test
    void criarHorarios_CriarESalvarHorario() {
        Horarios horario = criarHorario(LocalDateTime.of(2026, 8, 20, 9, 0), true);
        when(horarioRepository.save(horario)).thenReturn(horario);

        Horarios resultado = horarioService.criarHorarios(horario);

        assertThat(resultado).isEqualTo(horario);
        verify(horarioRepository).save(horario);
    }

    @Test
    void buscarPorId_HorarioExiste() {
        Horarios horario = criarHorario(LocalDateTime.of(2026, 8, 20, 9, 0), true);
        when(horarioRepository.findById(1L)).thenReturn(Optional.of(horario));

        Horarios resultado = horarioService.buscarPorId(1L);

        assertThat(resultado).isEqualTo(horario);
    }

    @Test
    void buscarPorId_HorarioNaoExiste_Exception() {
        when(horarioRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> horarioService.buscarPorId(99L))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Horario não encontrado");
    }

    @Test
    void editarHorario_AtualizarESalvar() {
        Horarios existente = criarHorario(LocalDateTime.of(2026, 8, 20, 9, 0), true);
        Horarios atualizacao = criarHorario(LocalDateTime.of(2026, 8, 21, 14, 0), false);
        when(horarioRepository.findById(1L)).thenReturn(Optional.of(existente));
        when(horarioRepository.save(any(Horarios.class))).thenAnswer(inv -> inv.getArgument(0));

        Horarios resultado = horarioService.editarHorario(1L, atualizacao);

        assertThat(resultado.getHorarioLivre()).isEqualTo(LocalDateTime.of(2026, 8, 21, 14, 0));
        assertThat(resultado.getDisponivel()).isFalse();
        verify(horarioRepository).save(existente);
    }

    @Test
    void editarHorario_HorarioNaoExiste_Exception() {
        when(horarioRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> horarioService.editarHorario(99L, criarHorario(LocalDateTime.now(), true)))
                .isInstanceOf(ResponseStatusException.class);

        verify(horarioRepository, never()).save(any());
    }

    @Test
    void deletarHorario_HorarioExiste_Delete() {
        Horarios horario = criarHorario(LocalDateTime.of(2026, 8, 20, 9, 0), true);
        when(horarioRepository.findById(1L)).thenReturn(Optional.of(horario));

        horarioService.deletarHorario(1L);

        verify(horarioRepository).delete(horario);
    }

    @Test
    void deletarHorario_HorarioNaoExiste_Exception() {
        when(horarioRepository.findById(67L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> horarioService.deletarHorario(67L))
                .isInstanceOf(ResponseStatusException.class);

        verify(horarioRepository, never()).delete(any());
    }

    @Test
    void criarHorarios_DataRetroativa_Exception() {
        Horarios horarioPassado = criarHorario(LocalDateTime.of(2020, 1, 1, 10, 0), true);

        assertThatThrownBy(() -> horarioService.criarHorarios(horarioPassado))
            .isInstanceOf(ResponseStatusException.class)
            .hasMessageContaining("Horário deve ser uma data futura");

        verify(horarioRepository, never()).save(any());
    }
}
