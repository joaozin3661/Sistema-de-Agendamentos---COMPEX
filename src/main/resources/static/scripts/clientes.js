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

async function load_clients(){
    try{
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


// Função chamada quando você clica no botão "Editar" de um cliente
function abrirModalEditar(cliente) {
    // Preenche os inputs com os dados atuais do cliente
    document.getElementById('edit-id').value = cliente.id;
    document.getElementById('edit-nome').value = cliente.nome;
    document.getElementById('edit-cpf').value = cliente.cpf || '';
    document.getElementById('edit-idade').value = cliente.idade || '';

    // Mostra o modal na tela
    modal.showModal();
}

function fecharModal() {
    modal.close();
}

// Ouve o evento de envio do formulário
formEditar.addEventListener('submit', async function(event) {
    // Evita que a página recarregue ao enviar o formulário
    event.preventDefault();

    // Captura os dados digitados
    const id = document.getElementById('edit-id').value;
    const payload = {
        nome: document.getElementById('edit-nome').value.trim(),
        cpf: document.getElementById('edit-cpf').value.trim(),
        idade: Number(document.getElementById('edit-idade').value)
    };

    // Validação extra (o 'required' do HTML já ajuda muito)
    if (!payload.nome || !payload.cpf || payload.idade <= 0) {
        alert('Preencha nome, CPF e idade válidos.');
        return;
    }

    try {
        // Seu PUT request original (que estava correto!)
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
        
        // Atualiza a lista na tela
        load_clients(); 
    } catch (error) {
        console.error(error);
        alert('Não foi possível atualizar o cliente.');
    }
});

async function editarCliente(cliente) {
    const nome = prompt('Novo nome do cliente:', cliente.nome);
    if (nome === null) return;

    const cpf = prompt('Novo CPF:', cliente.cpf || '');
    if (cpf === null) return;

    const idadeInput = prompt('Nova idade:', cliente.idade ?? '');
    if (idadeInput === null) return;

    const payload = {
        nome: nome.trim(),
        cpf: cpf.trim(),
        idade: Number(idadeInput)
    };

    if (!payload.nome || !payload.cpf || Number.isNaN(payload.idade) || payload.idade <= 0) {
        alert('Preencha nome, CPF e idade válidos.');
        return;
    }

    try {
        const response = await fetch(`${API}/${cliente.id}`, {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(payload)
        });

        if (!response.ok) {
            const errorText = await response.text();
            throw new Error(`Erro ao editar cliente: ${response.status} ${errorText}`);
        }

        alert('Cliente atualizado com sucesso!');
        carregarClientes();
    } catch (error) {
        console.error(error);
        alert('Não foi possível atualizar o cliente.');
    }
}

async function deletarCliente(id) {
    const confirmado = confirm('Deseja realmente excluir este cliente?');
    if (!confirmado) return;

    try {
        const response = await fetch(`${API}/${id}`, { method: 'DELETE' });

        if (!response.ok) {
            throw new Error(`Erro ao deletar cliente: ${response.status}`);
        }

        alert('Cliente deletado com sucesso!');
        carregarClientes();
    } catch (error) {
        console.error(error);
        alert('Não foi possível deletar o cliente.');
    }
}



load_clients()