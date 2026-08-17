const API = 'http://localhost:8080/api/horarios';
const area = document.getElementById('free_horarios')
const form = document.getElementById('new_horario')
const inputHorario = document.getElementById('horarioLivre')

if (inputHorario) {
    const agora = new Date(Date.now() - new Date().getTimezoneOffset() * 60000);
    inputHorario.min = agora.toISOString().slice(0, 16);
}

const DURACAO_ATENDIMENTO_MS = 60 * 60 * 1000;

function formatarIntervalo(horarioLivre) {
    const inicio = new Date(horarioLivre);
    const fim = new Date(inicio.getTime() + DURACAO_ATENDIMENTO_MS);

    const dataFormatada = inicio.toLocaleDateString('pt-BR');
    const horaInicio = inicio.toLocaleTimeString('pt-BR', { hour: '2-digit', minute: '2-digit' });
    const horaFim = fim.toLocaleTimeString('pt-BR', { hour: '2-digit', minute: '2-digit' });

    return `${dataFormatada} · ${horaInicio} – ${horaFim}`;
}

async function deletarHorario(id) {
    const confirmado = confirm('Deseja realmente excluir este horário?');
    if (!confirmado) return;

    try {
        const response = await fetch(`${API}/${id}`, { method: 'DELETE' });

        if (!response.ok) {
            throw new Error(`Erro ao deletar horário: ${response.status}`);
        }

        load_horarios();
    } catch (error) {
        console.error(error);
        alert('Não foi possível deletar o horário.');
    }
}

function criarLinhasHorarios(horario){
    const linha = document.createElement('div');
    linha.className = 'horario-linha';

    const horas = document.createElement('span');
    horas.className = 'horario-horas';
    horas.textContent = formatarIntervalo(horario.horarioLivre);

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
    deletar.addEventListener('click', () => deletarHorario(horario.id));

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

        area.innerHTML = "";

        if (!horarios.length)
        {
            area.textContent = "Sem horários disponíveis";
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

if (form) {
    form.addEventListener('submit', async (event) => {
        event.preventDefault();

        const payload = {
            horarioLivre: inputHorario.value
        };

        try {
            const response = await fetch(API, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(payload)
            });

            if (!response.ok) {
                const errorText = await response.text();
                throw new Error(`${response.status}: ${errorText}`);
            }

            form.reset();
            load_horarios();
        } catch (error) {
            console.error(error);
            if (error.message.startsWith('409')) {
                alert('Já existe um horário cadastrado nessa data/hora.');
            } else if (error.message.startsWith('400')) {
                alert('Horário inválido. Verifique se a data/hora está no futuro.');
            } else {
                alert('Não foi possível cadastrar o horário.');
            }
        }
    });
}

load_horarios()