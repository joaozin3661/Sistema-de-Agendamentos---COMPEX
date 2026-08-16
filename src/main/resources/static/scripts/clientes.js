const API = "http://localhost:8080/api/clientes";
const modal = document.getElementById('modal-editar');
const formEditar = document.getElementById('form-editar');
const area = document.getElementById('lista_clientes');


function fazerLinhaCliente(cliente){
    const linha = document.createElement('div');
    linha.className = 'cliente-linha';

    const nome = document.createElement('span');
    nome.className = 'cliente-nome';
    nome.textContent = cliente.nome;

    const actions = document.createElement('div');
    actions.className = 'cliente-actions';

    const editar = document.createElement('button');
    editar.type = 'button';
    editar.className = 'btn-editar';
    editar.textContent = 'Editar';
    editar.addEventListener('click', () => abrirModalEditar(cliente));

    const deletar = document.createElement('button');
    deletar.type = 'button';
    deletar.className = 'btn-deletar';
    deletar.textContent = 'Deletar';
    deletar.addEventListener('click', () => deletarCliente(cliente.id));
    
    actions.append(editar, deletar);
    linha.append(nome, actions);
    
    return linha;


}

// Carrega a lista
async function load_clients(){
    try {        
        const response = await fetch(API);
        if (!response.ok) {
            throw new Error(`Erro ao carregar cliente: ${response.status}`)
        }

        const clientes = await response.json();
    
        area.innerHTML = "Clientes <br><br>";

        // Se não houver clientes, mensagem para add um, senão, mostrar clientela
        if (!clientes.length)
        {
            area.innerHTML += "Não há clientes cadastrados. Pressione  ''+ Novo Cliente '' para adicionar um";
        }
        else
        {
            clientes.forEach((cliente) => {
                area.appendChild(fazerLinhaCliente(cliente));
            });
        }
        
    } 
    catch (error) 
    {
        area.textContent = "Falha na leitura dos clientes"
        console.error("Erro de carregamento", error);
    }
}

// Preenche e abre o modal
function abrirModalEditar(cliente) {    
    document.getElementById('edit-id').value = cliente.id;
    document.getElementById('edit-nome').value = cliente.nome;
    document.getElementById('edit-cpf').value = cliente.cpf || '';
    document.getElementById('edit-idade').value = cliente.idade || '';

    // Mostra o modal na tela
    modal.showModal();
}

// Fecha o modal
function fecharModal() {
    modal.close();
}

// Ouve o evento de salvar o formulário do modal de edição
formEditar.addEventListener('submit', async function(event) {
    event.preventDefault();

    const id = document.getElementById('edit-id').value;
    const payload = {
        nome: document.getElementById('edit-nome').value.trim(),
        cpf: document.getElementById('edit-cpf').value.trim(),
        idade: Number(document.getElementById('edit-idade').value)
    };

    if (!payload.nome || !payload.cpf || payload.idade <= 0) {
        alert('Preencha nome, CPF e idade válidos.');
        return;
    }

    try {
        const response = await fetch(`${API}/${id}`, {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(payload)
        });

        if (!response.ok) {
            const errorText = await response.text();
            throw new Error(`Erro ao editar cliente: ${response.status} ${errorText}`);
        }

        alert('Cliente atualizado com sucesso!');
        fecharModal();
        
    
        load_clients(); 
    } catch (error) {
        console.error(error);
        alert('Não foi possível atualizar o cliente.');
    }
});

async function deletarCliente(id) {
    const confirmado = confirm('Deseja realmente excluir este cliente?');
    if (!confirmado) return;

    try {
        const response = await fetch(`${API}/${id}`, { method: 'DELETE' });

        if (!response.ok) {
            throw new Error(`Erro ao deletar cliente: ${response.status}`);
        }

        alert('Cliente deletado com sucesso!');
        
        load_clients();
    } catch (error) {
        console.error(error);
        alert('Não foi possível deletar o cliente.');
    }
}

// Carrega os clientes ao abrir a página pela primeira vez
load_clients();