const API_AGENDAMENTOS = 'http://localhost:8080/api/agendamentos';
const API_CLIENTES = 'http://localhost:8080/api/clientes';
const API_HORARIOS = 'http://localhost:8080/api/horarios';

const form = document.getElementById('novoAgendamento');
const selectCliente = document.getElementById('cliente');
const selectHorario = document.getElementById('horario');
const listaAgendamentos = document.getElementById('lista_agendamentos');

const DURACAO_ATENDIMENTO_MS = 60 * 60 * 1000;

function formatarIntervalo(horarioLivre) {
    const inicio = new Date(horarioLivre);
    const fim = new Date(inicio.getTime() + DURACAO_ATENDIMENTO_MS);

    const dataFormatada = inicio.toLocaleDateString('pt-BR');
    const horaInicio = inicio.toLocaleTimeString('pt-BR', { hour: '2-digit', minute: '2-digit' });
    const horaFim = fim.toLocaleTimeString('pt-BR', { hour: '2-digit', minute: '2-digit' });

    return `${dataFormatada} · ${horaInicio} – ${horaFim}`;
}

async function carregarClientes() {
    try {
        const response = await fetch(API_CLIENTES);
        if (!response.ok) throw new Error(`Erro ao carregar clientes: ${response.status}`);

        const clientes = await response.json();

        clientes.forEach(cliente => {
            const option = document.createElement('option');
            option.value = cliente.id;
            option.textContent = cliente.nome;
            selectCliente.appendChild(option);
        });
    } catch (error) {
        console.error(error);
    }
}

async function carregarHorariosDisponiveis() {
    try {
        const response = await fetch(API_HORARIOS);
        if (!response.ok) throw new Error(`Erro ao carregar horários: ${response.status}`);

        const horarios = await response.json();

        selectHorario.querySelectorAll('option:not(:first-child)').forEach(o => o.remove());

        horarios
            .filter(horario => horario.disponivel)
            .forEach(horario => {
                const option = document.createElement('option');
                option.value = horario.id;
                option.textContent = formatarIntervalo(horario.horarioLivre);
                selectHorario.appendChild(option);
            });
    } catch (error) {
        console.error(error);
    }
}

async function cancelarAgendamento(id) {
    const confirmado = confirm('Deseja realmente cancelar este agendamento?');
    if (!confirmado) return;

    try {
        const response = await fetch(`${API_AGENDAMENTOS}/${id}/cancelar`, { method: 'PUT' });

        if (!response.ok) {
            throw new Error(`Erro ao cancelar agendamento: ${response.status}`);
        }

        carregarAgendamentos();
        carregarHorariosDisponiveis();
    } catch (error) {
        console.error(error);
        alert('Não foi possível cancelar o agendamento.');
    }
}

function criarLinhaAgendamento(agendamento) {
    const linha = document.createElement('div');
    linha.className = 'agendamento-linha';

    const cliente = document.createElement('span');
    cliente.className = 'agendamento-cliente';
    cliente.textContent = agendamento.cliente.nome;

    const horario = document.createElement('span');
    horario.className = 'agendamento-horario';
    horario.textContent = formatarIntervalo(agendamento.horarios.horarioLivre);

    const cancelar = document.createElement('button');
    cancelar.type = 'button';
    cancelar.className = 'agendamento-cancelar-btn';
    cancelar.textContent = 'Cancelar';
    cancelar.addEventListener('click', () => cancelarAgendamento(agendamento.id));

    linha.append(cliente, horario, cancelar);
    return linha;
}

async function carregarAgendamentos() {
    try {
        const response = await fetch(API_AGENDAMENTOS);
        if (!response.ok) throw new Error(`Erro ao carregar agendamentos: ${response.status}`);

        const agendamentos = await response.json();

        listaAgendamentos.innerHTML = '';

        if (!agendamentos.length) {
            listaAgendamentos.textContent = 'Nenhum agendamento ativo no momento.';
            return;
        }

        agendamentos.forEach(agendamento => {
            listaAgendamentos.appendChild(criarLinhaAgendamento(agendamento));
        });
    } catch (error) {
        listaAgendamentos.textContent = 'Falha ao carregar agendamentos.';
        console.error(error);
    }
}

if (form) {
    form.addEventListener('submit', async (event) => {
        event.preventDefault();

        const payload = {
            clienteId: Number(selectCliente.value),
            horarioId: Number(selectHorario.value)
        };

        try {
            const response = await fetch(API_AGENDAMENTOS, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(payload)
            });

            if (!response.ok) {
                const errorText = await response.text();
                throw new Error(`${response.status}: ${errorText}`);
            }

            form.reset();
            carregarAgendamentos();
            carregarHorariosDisponiveis();
        } catch (error) {
            console.error(error);
            if (error.message.startsWith('409')) {
                alert('Esse horário já está ocupado.');
            } else if (error.message.startsWith('404')) {
                alert('Cliente ou horário não encontrado.');
            } else {
                alert('Não foi possível criar o agendamento.');
            }
        }
    });
}

carregarClientes();
carregarHorariosDisponiveis();
carregarAgendamentos();
