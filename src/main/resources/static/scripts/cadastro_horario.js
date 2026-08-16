const API = 'http://localhost:8080/api/horarios';
const area = document.getElementById('free_horarios')




function criarLinhasHorarios(horario){
    const linha = document.createElement('div');
    linha.className = 'horario-linha';

    const horas = document.createElement('span');
    horas.className = 'horario-horas';
    horas.textContent = horario.horarioLivre;

    const status = document.createElement('span');
    status.className = 'horario-disponivel';
    if (!horario.disponivel)
    {
        status.textContent = 'Ocupado'
    }
    else
    {
        status.textContent = 'Disponível'
    }

    const deletar = document.createElement('button');
    deletar.type = 'button';
    deletar.className = 'horario-deletar-btn';
    deletar.textContent = 'Deletar';
    deletar.addEventListener('click', () => deletarHorario());

    linha.append(horas, status, deletar);

    return linha;
}


async function load_horarios() {
    try {
        const response = await fetch(API);

        if (!response.ok){
            throw new Error(`Erro ao carregar horários: ${response.status}`);
        }

        const horarios = await response.json();

        if (!horarios.length)
        {
            area.innerHTML = "Sem horários disponíveis";
        }
        else
        {
            horarios.forEach(horario => {
                area.appendChild(criarLinhasHorarios(horario)); // inserir função de horário
            });
        }
    } catch (error) {
        area.textContent = 'Falha na leitura dos dados'
        console.error("Erro de carregamento: ", error)
    }
    
}

load_horarios()