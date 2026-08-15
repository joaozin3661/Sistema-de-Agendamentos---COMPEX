const form = document.getElementById('newCliente');
const API = "http://localhost:8080/api/clientes"


if (form) {
    form.addEventListener('submit', async (event) => {
        event.preventDefault();

        const payload = {
            nome: document.getElementById('nome').value,
            cpf: document.getElementById('cpf').value,
            idade: Number(document.getElementById('idade').value)
        };

        try {
            const response = await fetch(API, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(payload)
            });

            if (!response.ok) {
                let mensagem = "Erro ao cadastrar cliente."
                const errorText = await response.text();
                if (response.status == 409)
                {
                    alert("Erro 409: Já existe esse CPF na lista de clientes");
                }
                form.reset();
                throw new Error(`Erro ao cadastrar cliente: ${response.status} ${errorText}`);
            }
                

            console.log(await response.json());
            alert("Cliente cadastrado com sucesso!")
            form.reset();
            window.location.href='clientes.html'
        } catch (e) {
            console.error(e);
        }
    });
}