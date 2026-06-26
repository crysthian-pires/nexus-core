# Nexus API

API REST desenvolvida com Spring Boot, focada em autenticação, segurança e boas práticas de arquitetura.

## 🚀 Tecnologias

- Java 21
- Spring Boot 4
- Spring Security + JWT
- PostgreSQL + Flyway
- Docker
- Swagger / OpenAPI 3

## 📋 Pré-requisitos

- Java 21
- Docker Desktop

## ⚙️ Como rodar

### 1. Clone o repositório
```bash
git clone https://github.com/crysthian-pires/Nexus.git
cd Nexus/core
```

### 2. Suba o banco de dados
```bash
docker-compose up -d
```

### 3. Configure as variáveis de ambiente
Crie um arquivo `application-local.properties` em `src/main/resources` com:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/nexus
spring.datasource.username=nexus
spring.datasource.password=nexus123
spring.flyway.url=jdbc:postgresql://localhost:5432/nexus
spring.flyway.user=nexus
spring.flyway.password=nexus123
jwt.secret=sua-chave-secreta-aqui
```

### 4. Rode a aplicação
```bash
./mvnw spring-boot:run
```

### 5. Acesse a documentação
```
http://localhost:8080/swagger-ui/index.html
```

## 🔐 Autenticação

A API usa JWT com access token e refresh token.

### Fluxo:
1. `POST /auth/login` → retorna `accessToken` (15min) e `refreshToken` (7 dias)
2. Envie o `accessToken` no header: `Authorization: Bearer <token>`
3. Quando expirar, use `POST /auth/refresh` para renovar
4. `POST /auth/logout` revoga o refresh token

## 📌 Endpoints

### Autenticação
| Método | Rota | Descrição | Auth |
|--------|------|-----------|------|
| POST | `/auth/login` | Login | ❌ |
| POST | `/auth/refresh` | Renovar token | ❌ |
| POST | `/auth/logout` | Logout | ❌ |

### Usuários
| Método | Rota | Descrição | Auth |
|--------|------|-----------|------|
| POST | `/users` | Criar conta | ❌ |
| GET | `/users` | Listar todos | ADMIN |
| GET | `/users/{id}` | Buscar por ID | ✅ |
| PATCH | `/users/{id}` | Atualizar dados | ✅ |
| PATCH | `/users/{id}/profile` | Atualizar perfil | ✅ |
| DELETE | `/users/{id}` | Desativar usuário | ADMIN |

## 🧪 Testes

```bash
./mvnw test
```

## 🐳 Docker

```bash
# Subir banco
docker-compose up -d

# Parar banco
docker-compose down

# Parar e remover dados
docker-compose down -v
```

## 📁 Estrutura do projeto

```
src/
├── main/
│   ├── java/com/nexus/core/
│   │   ├── controller/       # PingController
│   │   ├── exception/        # Exceções customizadas
│   │   ├── security/         # JWT, filtros e configurações
│   │   └── user/             # Model, Repository, Service, Controller e DTOs
│   └── resources/
│       ├── db/migration/     # Scripts Flyway
│       └── http/             # Arquivos de teste HTTP
```

## 📄 Licença

MIT
