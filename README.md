# cms-local

> **Este README é bilíngue** — Português (Brasil) primeiro, versão em inglês logo abaixo.
> **This README is bilingual** — Brazilian Portuguese first, English version below.

---

## Sobre o projeto (pt-BR)

Um CMS local-first que edita páginas servidas por FTP: baixa as páginas HTML,
marca os blocos editáveis (`data-editable`), permite editar texto e imagens por
uma interface web e envia o resultado de volta — mantendo um histórico de
revisões de cada salvamento.

O contrato completo de requisitos está em [`docs/requisitos.md`](docs/requisitos.md).

## Pré-requisitos

| Ferramenta | Versão | Observações |
|---|---|---|
| JDK | 21 | runtime + compilador |
| Docker + Compose | 29.x / v5.4.x | executa o PostgreSQL 16 |
| Node | 24.x | apenas para o servidor FTP de teste |
| Maven | — | não é preciso: o wrapper (`./mvnw`) já vem no repositório |

## Como rodar — três passos

### 1. Banco de dados

O arquivo Compose precisa de valores do `.env` (gitignored, de propósito). Copie
o template e preencha — os placeholders dizem o que cada valor é:

    cp .env.example .env    # depois edite o .env com valores locais reais

    docker compose up -d    # PostgreSQL 16, volume nomeado `pgdata`, porta 5432 no host

### 2. Aplicação (Spring Boot)

A aplicação resolve a senha do banco a partir do ambiente do sistema operacional —
defina antes de iniciar:

    export SPRING_DATASOURCE_PASSWORD='o valor que você colocou no .env'

    ./mvnw spring-boot:run

O Flyway aplica as migrações automaticamente na primeira inicialização
(`Successfully applied 1 migration`); a aplicação escuta na porta 8080.

### 3. Servidor FTP de teste (necessário apenas ao testar funcionalidades de FTP)

    cd tools/ftp-server
    npm install
    npm start              # escuta em ftp://127.0.0.1:2121 (passivo 2130-2140)

Login: `cms_test` / a senha descartável definida em `tools/ftp-server/server.js`
(commitada de propósito — são credenciais de teste descartáveis; credenciais reais
não devem aparecer em lugar nenhum).

## De onde vêm os segredos (e onde nunca devem estar)

| Valor | Onde vive | Como chega à aplicação |
|---|---|---|
| Senha do container do banco | `.env` (gitignored) | interpolação do Compose → `POSTGRES_PASSWORD` |
| Senha do banco para a aplicação | ambiente do seu shell | export no `~/.bashrc` → `${SPRING_DATASOURCE_PASSWORD}` no `application.yaml` |
| Credenciais FTP de teste | `tools/ftp-server/server.js` | commitadas de propósito (descartáveis) |

Regra: o repositório serve apenas placeholders — `.env.example` e `application.yaml`
não carregam valores reais, e valor real nenhum pertence à documentação.

## Estrutura do projeto

    src/main/resources/db/migration/   migrações Flyway (fonte de verdade do schema)
    docs/requisitos.md                 contrato de requisitos
    tools/ftp-server/                  servidor FTP de teste local (Node)

---

## About the project (English)

A local-first CMS that edits pages served over FTP: it downloads HTML pages,
marks editable blocks (`data-editable`), lets you edit text and images through
a web UI, and uploads the result back — keeping a revision history of every save.

The full requirements contract lives in [`docs/requisitos.md`](docs/requisitos.md).

## Prerequisites

| Tool | Version | Notes |
|---|---|---|
| JDK | 21 | runtime + compiler |
| Docker + Compose | 29.x / v5.4.x | runs PostgreSQL 16 |
| Node | 24.x | only for the FTP test server |
| Maven | — | not needed: the wrapper (`./mvnw`) ships in the repo |

## Running it — three steps

### 1. Database

The Compose file needs values from `.env` (gitignored, by design). Copy the
template and fill it in — the placeholders tell you what each value is:

    cp .env.example .env    # then edit .env with real local values

    docker compose up -d    # PostgreSQL 16, named volume `pgdata`, host port 5432

### 2. Application (Spring Boot)

The app resolves its DB password from the OS environment — set it before starting:

    export SPRING_DATASOURCE_PASSWORD='the value you put in .env'

    ./mvnw spring-boot:run

Flyway applies migrations automatically on first start (`Successfully applied 1
migration`); the app listens on 8080.

### 3. FTP test server (only needed when testing FTP features)

    cd tools/ftp-server
    npm install
    npm start              # listens on ftp://127.0.0.1:2121 (passive 2130-2140)

Login: `cms_test` / the throwaway password defined in `tools/ftp-server/server.js`
(deliberately committed — these credentials are disposable test doubles; real ones
must never appear anywhere).

## Where secrets come from (and where they must never be)

| Value | Lives in | Reaches the app via |
|---|---|---|
| DB container password | `.env` (gitignored) | Compose interpolation → `POSTGRES_PASSWORD` |
| App DB password | your shell env | `~/.bashrc` export → `${SPRING_DATASOURCE_PASSWORD}` in `application.yaml` |
| FTP test credentials | `tools/ftp-server/server.js` | committed on purpose (throwaway) |

Rule: the repo serves placeholders only — `.env.example` and `application.yaml`
carry no real values, and no real value belongs in documentation either.

## Project layout

    src/main/resources/db/migration/   Flyway migrations (schema source of truth)
    docs/requisitos.md                 requirements contract
    tools/ftp-server/                  local FTP test server (Node)

----
