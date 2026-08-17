package com.sistemaagendamento.compex.service;

import com.sistemaagendamento.compex.model.Agendamento;
import com.sistemaagendamento.compex.model.Cliente;
import com.sistemaagendamento.compex.model.Horarios;
import com.sistemaagendamento.compex.model.Status;
import com.sistemaagendamento.compex.repository.AgendamentoRepository;
import com.sistemaagendamento.compex.repository.ClienteRepository;
import com.sistemaagendamento.compex.repository.HorariosRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Service
public class AgendamentoService {

    private final AgendamentoRepository agendamentoRepository;
    private final ClienteRepository clienteRepository;
    private final HorariosRepository horariosRepository;

    public AgendamentoService(AgendamentoRepository a, ClienteRepository c, HorariosRepository h) {
        agendamentoRepository = a;
        this.clienteRepository = c;
        this.horariosRepository = h;
    }

    public List<Agendamento> listarAtivos() {
        LocalDateTime agora = LocalDateTime.now();
        return agendamentoRepository.findAll().stream()
                .filter(a -> a.getStatus() == Status.ATIVO)
                .filter(a -> a.getHorarios().getHorarioLivre().isAfter(agora))
                .sorted(Comparator.comparing(a -> a.getHorarios().getHorarioLivre()))
                .toList();
    }

    public Agendamento buscarPorId(Long id) {
        return agendamentoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Agendamento não encontrado"));
    }

    @Transactional
    public Agendamento criarAgendamento(Long clienteId, Long horarioId) {
        Cliente cliente = clienteRepository.findById(clienteId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente não encontrado"));

        Horarios horario = horariosRepository.findById(horarioId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Horário não encontrado"));

        boolean horarioJaOcupado = !agendamentoRepository
                .findByHorariosIdAndStatus(horarioId, Status.ATIVO)
                .isEmpty();

        if (horarioJaOcupado || !Boolean.TRUE.equals(horario.getDisponivel())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Horário já está ocupado");
        }

        Agendamento agendamento = new Agendamento();
        agendamento.setCliente(cliente);
        agendamento.setHorarios(horario);
        agendamento.setStatus(Status.ATIVO);

        horario.setDisponivel(false);
        horariosRepository.save(horario);

        return agendamentoRepository.save(agendamento);
    }

    @Transactional
    public Agendamento cancelarAgendamento(Long id) {
        Agendamento agendamento = buscarPorId(id);

        if (agendamento.getStatus() == Status.CANCELADO) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Agendamento já está cancelado");
        }

        agendamento.setStatus(Status.CANCELADO);

        Horarios horario = agendamento.getHorarios();
        horario.setDisponivel(true);
        horariosRepository.save(horario);

        return agendamentoRepository.save(agendamento);
    }
}
