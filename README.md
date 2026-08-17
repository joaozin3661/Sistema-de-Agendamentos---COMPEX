# Compex: Sistema de Agendamento

Sistema de agendamento para uma clínica de estética para a segunda fase do processo seletivo do COMPEX. 

## Tecnologias utilizadas por enquanto

- Java 26
- Spring Boot 4.1 (Web, Data JPA, Validation)
- PostgreSQL (bd principal)
- H2, Mockito e JUnit5 (para testes)
- Maven
- HTML/CSS/JS puro no front-end

## Como executar o projeto

Pré-requisitos
- Java 26 instalado
- PostgreSQL rodando localmente
- Um banco de dados criado chamado "agendamentos_compex"

### 1. Configurar a variável de ambiente do banco

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

### 2. Rodar a aplicação

Na raiz do projeto:
```bash
./mvnw spring-boot:run
```

A aplicação sobe em "http://localhost:8080".

## Funcionalidades implementadas/em processo de implementação/faltam implementar

Implementadas
- Modelagem de dados: "Cliente", "Horarios", "Agendamento", "Status" (ATIVO/CANCELADO)

- Regra de integridade a nível de banco: somente 1 agendamento ativo por horário

- API REST para cadastro de clientes ("ClienteController", "ClienteService", "ClienteRepository"), com validação de campos (nome, CPF único, idade) e proteção contra exclusão de cliente com agendamento vinculado (409)

- API REST para cadastro de horários disponíveis ("HorarioController", "HorarioService", "HorariosRepository"), com checagem de horário duplicado (409) e de horário no passado (400, `@Future`)

- API REST de agendamento completa ("AgendamentoController", "AgendamentoService"): criar agendamento (valida cliente/horário e ocupação), cancelar agendamento (libera o horário), listar agendamentos ativos (ordenados por data, só os futuros)

- Tela de cadastro/listagem/edição/exclusão de clientes ("clientes.html", "novo_cliente.html") integrada com a API

- Tela de cadastro/listagem/exclusão de horários ("cadastro_horario.html") integrada com a API

- Tela de agendamento ("agenda.html"): criar agendamento (cliente + horário disponível), listar agendamentos ativos, cancelar

- Testes unitários (JUnit 5 + Mockito) de "ClienteService", "HorarioService" e "AgendamentoService"

Falta implementar
- Filtro por cliente na listagem de agendamentos (hoje lista todos os ativos futuros, sem filtro por cliente)



## Estrutura do projeto

```
src/main/java/com/sistemaagendamento/compex/
├── CompexApplication.java
├── model/                (model com as entidades)
│   ├── Cliente.java 
│   ├── Horarios.java
│   ├── Agendamento.java
│   └── Status.java
├── repository/           (interfaces JpaRepository)
│   ├── ClienteRepository.java
│   ├── AgendamentoRepository.java
│   └── HorariosRepository.java
├── service/              (regras de negócio)
│   ├── ClienteService.java
│   ├── HorarioService.java
│   └── AgendamentoService.java
└── controller/           (endpoints REST)
    ├── ClienteController.java
    ├── HorarioController.java
    └── AgendamentoController.java
src/main/resources/
├── application.properties
├── schema.sql           (garantir 1 agendamento por horário)
└── static/              (front-end HTML/CSS/JS)
    ├── index.html
    ├── clientes.html / novo_cliente.html
    ├── cadastro_horario.html
    ├── agenda.html
    ├── scripts/
    └── styles/
```

## Validação de horário duplicado

A regra "não permitir dois clientes agendados no mesmo horário" será garantida no nível de banco e da camada de serviço:

1. Banco de dados: um índice único parcial em `agendamento(horario_id)` filtrando `WHERE status = 'ATIVO'` (arquivo `schema.sql`). Isso impede duplicidade mesmo em cenários de 2 requisições as mesmo tempo

2. Camada de serviço: o `AgendamentoService` checa se o horário já tem um agendamento ativo (via `AgendamentoRepository.findByHorariosIdAndStatus`) ou está marcado como indisponível antes de criar, retornando `409 Conflict` pro cliente em vez de deixar estourar erro de banco.

## Principais dificuldades encontradas

Garantir a regra de "um horário, um agendamento ativo" de forma segura sob concorrência foi mais do que uma checagem em Java, foi necessário eu fazer um índice único parcial no PostgreSQL, já que uma constraint única padrão ia bloquear agendamentos cancelados de liberar o horário.

