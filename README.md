-> Compex: Sistema de Agendamento

Sistema de agendamento para uma clínica de estética para a segunda fase do processo seletivo do COMPEX. 

-> Tecnologias utilizadas por enquanto

- Java 26
- Spring Boot 4.1 (Web, Data JPA, Validation)
- PostgreSQL (bd principal)
- H2 (para testes)
- Maven
- HTML/CSS puro no front-end

Como executar o projeto

Pré-requisitos
- Java 26 instalado
- PostgreSQL rodando localmente
- Um banco de dados criado chamado "agendamentos_compex"

1. Configurar a variável de ambiente do banco

A senha do banco não fica no código para segurança dos programadores, ela é lida da variável de ambiente "DB_PASSWORD".
Então tem que definir a sua senha do postgres no powershell/cmd

No PowerShell:
```powershell
$env:DB_PASSWORD="sua_senha_aqui"
```

No Git Bash / Linux / macOS:
```bash
export DB_PASSWORD=sua_senha_aqui
```

2. Rodar a aplicação

Na raiz do projeto:
```bash
./mvnw spring-boot:run
```

A aplicação sobe em "http://localhost:8080".

-> Funcionalidades implementadas/em processo de implementação/faltam implementar

Implementadas
- Modelagem de dados: "Cliente", "Horarios", "Agendamento", "Status" (ATIVO/CANCELADO)
- Regra de integridade a nível de banco: somente 1 agendamento ativo por horário
- Tela estática inicial (front-end)

Em processo de implementação
- O restante das telas

Falta implementar
- API REST para cadastro de clientes
- API REST para cadastro de horários disponíveis
- API REST para agendamento de horário
- API REST para cancelamento de agendamento
- Endpoint de listagem dos próximos agendamentos


-> Estrutura do projeto

```
src/main/java/com/sistemaagendamento/compex/
├── CompexApplication.java
└── model/                (model com as entidades)
    ├── Cliente.java 
    ├── Horarios.java
    ├── Agendamento.java
    └── Status.java
src/main/resources/
├── application.properties
├── schema.sql           (garantir 1 agendamento por horário)
└── static/              (front-end (HTML/CSS))
```

-> Validação de horário duplicado

A regra "não permitir dois clientes agendados no mesmo horário" será garantida no nível de banco e da camada de serviço:

1. Banco de dados: um índice único parcial em `agendamento(horario_id)` filtrando `WHERE status = 'ATIVO'` (arquivo `schema.sql`). Isso impede duplicidade mesmo em cenários de 2 requisições as mesmo tempo

2. Camada de serviço (em desenvolvimento): checagem de disponibilidade de horário, vai retornar pro cliente um erro de serviço e não de banco.

-> Principal dificuldade encontrada

Garantir a regra de "um horário, um agendamento ativo" de forma segura sob concorrência foi mais do que uma checagem em Java, foi necessário eu fazer um índice único parcial no PostgreSQL, já que uma constraint única padrão ia bloquear agendamentos cancelados de liberar o horário.

