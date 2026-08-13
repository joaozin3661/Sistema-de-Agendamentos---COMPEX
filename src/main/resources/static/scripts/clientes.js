const API = "http://localhost:8080/api/clientes"
const area = document.getElementById('lista_clientes')

fetch(API)
    .then(response =>{
        if (!response.ok) {
            throw new Error("Erro de conexão 500")
        }

        return response.text();
    })

    .then(data =>{
        area.innerHTML = "Clientes <br><br>" + data;
    })
    .catch (error => {
        area.textContent = "Failed to load contend"
        console.error("Erro de carregamento", error);
        
    });