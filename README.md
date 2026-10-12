# 🍽️ Brasa & Sal — API

API REST para gestão de comandas de restaurante. Controla mesas, clientes, cardápio e faturamento.

## ✨ Funcionalidades

- **Clientes** — cadastro, edição e abertura de comandas
- **Comandas** — adicionar/remover produtos, alterar quantidade, fechar e liberar mesa
- **Cardápio** — CRUD de produtos com controle de disponibilidade
- **Dashboard** — KPIs do salão em tempo real
- **Relatórios** — faturamento dos últimos 7 dias

## 🛠️ Stack

- Java 17
- Spring Boot 3.5
- Spring Data JPA / Hibernate
- PostgreSQL
- Maven

## 🏗️ Arquitetura

```
src/main/java/com/brasaesal/
├── controller/   # Endpoints REST
├── service/      # Regras de negócio
├── repository/   # Acesso a dados (JPA)
├── model/        # Entidades
├── dto/          # Objetos de transferência
└── exception/    # Tratamento de erros
```

## 🚀 Como rodar

### Pré-requisitos
- Java 17+
- Maven
- PostgreSQL 14+

### Passos

```bash
# 1. Clona o repo
git clone https://github.com/tiagobarboza-dev/brasa-sal-api.git
cd brasa-sal-api

# 2. Cria o banco e roda o script
# Execute db/criar-banco.sql no PostgreSQL

# 3. Configura as variáveis de ambiente
# DB_URL=jdbc:postgresql://localhost:5432/brasa_sal
# DB_USERNAME=postgres
# DB_PASSWORD=sua_senha

# 4. Sobe o backend
mvn spring-boot:run
```

API disponível em `http://localhost:8080/api`

## 📡 Endpoints principais

| Método | Rota | Descrição |
|--------|------|-----------|
| GET | `/api/clientes` | Lista clientes |
| POST | `/api/clientes` | Cadastra cliente |
| GET | `/api/comandas` | Lista comandas |
| POST | `/api/clientes/{id}/comandas` | Abre comanda |
| POST | `/api/comandas/{id}/itens` | Adiciona item |
| PATCH | `/api/comandas/{id}/fechar` | Fecha comanda |
| PATCH | `/api/comandas/{id}/liberar-mesa` | Libera mesa |
| GET | `/api/dashboard` | KPIs do salão |
| GET | `/api/relatorios/resumo` | Faturamento 7 dias |

## 🎯 Decisões de design

- **Regras de negócio centralizadas no backend** — o front nunca calcula total nem valida estado
- **Status de comanda como enum** — `ABERTA`, `FECHADA`, `LIBERADA`
- **DTOs para entrada/saída** — entidades JPA nunca expostas diretamente
- **Tratamento de exceções customizado** — respostas de erro consistentes

## 🔗 Frontend

Interface web: [comandas-web](https://github.com/tiagobarboza-dev/comandas-web)

## 👤 Autor

**Tiago Barboza**
- GitHub: [@tiagobarboza-dev](https://github.com/tiagobarboza-dev)
