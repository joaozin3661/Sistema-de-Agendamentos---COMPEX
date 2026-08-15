const API = "http://localhost:8080/api/clientes"
const area = document.getElementById('lista_clientes')


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
    editar.addEventListener('click', () => editarCliente(cliente));

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


async function editarCliente(params) {
    
}

async function deletarCliente(id) {
    
}



load_clients()