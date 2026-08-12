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
                const errorText = await response.text();
                throw new Error(`Erro ao cadastrar cliente: ${response.status} ${errorText}`);
            }

            console.log(await response.json());
            form.reset();
        } catch (e) {
            console.error(e);
        }
    });
}