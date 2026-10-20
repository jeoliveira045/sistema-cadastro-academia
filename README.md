# Sistema de Usuários da Academia

API REST que cadastra os usuários da academia (alunos, professores, administradores) e
faz a autenticação com JWT.

Este sistema é a fonte dos cadastros de alunos e professores. O
[sistema-academia](../sistema-academia), que faz o monitoramento, importa esses dados
usando a conta de serviço `service-account`.

## Tecnologias

- Java 17+ (o `.sdkmanrc` usa `21.0.2-open`; a imagem Docker usa Java 21)
- Spring Boot 4.1 (Web MVC + Data JDBC)
- Spring Security com OAuth2 Resource Server e JWT (jjwt)
- PostgreSQL (com a extensão `pgcrypto` para as senhas iniciais)
- Flyway para as migrations
- Spring Boot Actuator
- Docker e Kubernetes (minikube + kustomize)

## Estrutura

```
src/main/java/monokai
├── config/      # Segurança (JWT) e Flyway
├── controller/  # Auth, alunos, professores, usuários e roles
├── model/       # Pessoa, Aluno, Professor, Usuario, Role e enums
├── repository/  # Acesso ao banco
└── service/     # Cadastro, ativação/inativação de aluno, JWT e UserDetails
```

As migrations ficam em `src/main/resources/db/migration` e rodam ao subir a aplicação.

## Configuração

A aplicação lê o banco de variáveis de ambiente:

| Variável | Exemplo |
|---|---|
| `SPRING_DATASOURCE_URL` | `jdbc:postgresql://localhost:5432/usuario-academia` |
| `SPRING_DATASOURCE_USERNAME` | `usuario-academia` |
| `SPRING_DATASOURCE_PASSWORD` | senha definida no `docker-compose.yml` |

O segredo e a validade do JWT ficam em `application.properties` (`jwt.secret` e
`jwt.expiration`, em milissegundos).

## Como rodar

### Local (só o banco no Docker)

```bash
docker compose up -d

export SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/usuario-academia
export SPRING_DATASOURCE_USERNAME=usuario-academia
export SPRING_DATASOURCE_PASSWORD=<senha do docker-compose.yml>

mvn spring-boot:run
```

A API sobe em `http://localhost:8080`.

### Aplicação e banco no Docker

```bash
docker compose -f docker-compose-system.yml up -d --build
```

> Hoje o `docker-compose-system.yml` tem marcadores de conflito de merge (`<<<<<<<`) no
> nome da imagem. É preciso resolver antes de usar esse arquivo.

### Kubernetes (minikube)

Os manifestos ficam em `k8s/base`, com o overlay de desenvolvimento em `k8s/overlays/dev`.
O script `redeploy.sh` gera a imagem `sistema-usuario:1.0.0` dentro do Docker do minikube
e aplica os manifestos de novo:

```bash
./redeploy.sh
minikube service java-app-service --url   # endereço usado no api.baseurl do sistema-academia
```

> Tanto o `docker compose` local quanto o do sistema-academia usam a porta `5432`. Para
> rodar os dois bancos ao mesmo tempo, mude a porta de um deles.

## Autenticação

1. Faça login para receber o token:

   ```bash
   curl -X POST http://localhost:8080/auth/login \
     -H 'Content-Type: application/json' \
     -d '{"username": "admin", "password": "admin"}'
   ```

2. Mande o token nas outras requisições:

   ```bash
   curl http://localhost:8080/alunos -H "Authorization: Bearer <token>"
   ```

As roles vão no claim `roles` do JWT e viram `ROLE_<nome>` no Spring Security.

### Usuários criados pelas migrations

| Usuário | Senha | Role | Uso |
|---|---|---|---|
| `admin` | `admin` | `ADMIN` | Administração |
| `service-account` | `service-account` | `SERVICE` | Importação feita pelo sistema-academia |

Essas senhas são só para desenvolvimento. Troque antes de usar em outro ambiente.

## Endpoints

| Rota | Quem acessa |
|---|---|
| `POST /auth/login` | Público |
| `GET /actuator/health` | Público |
| `GET /alunos` | `ADMIN`, `PROFESSOR`, `SERVICE` |
| `POST /alunos`, `GET/PUT/DELETE /alunos/{id}`, `PATCH /alunos/{id}/situacao` | `ADMIN`, `PROFESSOR` |
| `/professores` e `/professores/{id}` (GET, POST, PUT, DELETE) | `ADMIN`, `SERVICE` |
| `/roles` e `/roles/{id}` | `ADMIN`, `PROFESSOR` |
| `/usuarios`, `/usuarios/{id}`, `DELETE /usuarios/{username}` | Qualquer usuário autenticado |

`PATCH /alunos/{id}/situacao` troca o aluno entre `ATIVO` e `INATIVO`.

Exemplos de corpo para POST e PUT ficam em `docs/requests/*.json`.

## Testes

```bash
mvn test
```

Os testes cobrem os controllers e os services (cadastro, JWT, ativação/inativação de aluno
e carregamento de usuários).

## Documentação

Os diagramas em PlantUML ficam em `docs/`: casos de uso, classes do modelo, classes dos
services e diagrama de sequência.
