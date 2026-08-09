package com.sistemaagendamento.compex.model;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "horario_disponivel")
public class Horarios {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDateTime horarioLivre;

    @Column(nullable = false)
    private Boolean disponivel=true;

public Horarios(){
}

public Long getId(){
    return id;
}
public LocalDateTime getHorarioLivre(){
    return horarioLivre;
}
public Boolean getDisponivel(){
    return disponivel;
}
public void setHorarioLivre(LocalDateTime horarioLivre){
    this.horarioLivre=horarioLivre;
}
public void setDisponivel(Boolean disponivel){
    this.disponivel = disponivel;
}

}
