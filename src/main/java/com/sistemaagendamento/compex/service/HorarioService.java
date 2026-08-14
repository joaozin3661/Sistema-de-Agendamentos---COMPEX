package com.sistemaagendamento.compex.service;


import com.sistemaagendamento.compex.model.Horarios;
import com.sistemaagendamento.compex.repository.HorariosRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
@Service
public class HorarioService {
    private HorariosRepository horarioRepository;

    public HorarioService(HorariosRepository horarioRepository){this.horarioRepository = horarioRepository;}

    public List<Horarios> listarHorarios(){
        List<Horarios> lista = horarioRepository.findAll();
        return lista;
    }

    public Horarios criarHorarios(Horarios horarios){
        if (horarioRepository.existsByHorarioLivre(horarios.getHorarioLivre())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Já existe um horário cadastrado nessa data/hora");
        }
        return horarioRepository.save(horarios);
    }

    public Horarios buscarPorId(Long id) {
        return horarioRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Horario não encontrado"));
    }

    public Horarios editarHorario(Long id, Horarios horarios){
        Horarios horarioExistente = buscarPorId(id);

        horarioExistente.setHorarioLivre(horarios.getHorarioLivre());
        horarioExistente.setDisponivel(horarios.getDisponivel());

        return horarioRepository.save(horarioExistente);
    }

    public void deletarHorario(Long id){
        Horarios horarios = buscarPorId(id);

        horarioRepository.delete(horarios);
    }



}
