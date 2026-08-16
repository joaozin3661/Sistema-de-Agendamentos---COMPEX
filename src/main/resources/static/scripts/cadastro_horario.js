const API = 'http://localhost:8080/api/horarios';
const area = document.getElementById('free_horarios')




async function load_horarios() {
    try {
        const response = await fetch(API);

        if (!response.ok){
            throw new Error(`Erro ao carregar horários: ${response.status}`);
        }

        const horarios = await response.json();

        if (!horarios.lenght)
        {
            area.innerHTML = "Sem horários disponíveis";
        }
        else
        {
            horarios.forEach(horario => {
                area.appendChild() // inserir função de horário
            });
        }
    } catch (error) {
        area.textContent = 'Falha na leitura dos dados'
        console.error("Erro de carregamento: ", error)
    }
    
}

load_horarios()