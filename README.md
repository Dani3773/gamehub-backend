# GameHub - API REST

API REST de uma loja digital de jogos (inspirada na Steam), desenvolvida em
Spring Boot 3 como Trabalho 1 da disciplina.

**Equipe:**
| Integrante | Entidade principal | GitHub |
|---|---|---|
| Daniel Felisberto | Usuario | |
| Gustavo Marcelino | Jogo | |
| Igor Rayciki Figueredo | Categoria | |
| Lucas Guollo | Compra | |
| Todos | Avaliacao | |

## Como rodar o projeto

1. Subir o banco Postgres:
```bash
   docker compose up -d
```
> Se a porta 5432 já estiver em uso na sua máquina (Postgres local
> instalado), o `docker-compose.yml` já está mapeado para a porta 5433.
2. Rodar a aplicação:
```bash
   ./mvnw spring-boot:run
```
3. API: `http://localhost:8080`
4. Swagger UI: `http://localhost:8080/swagger-ui.html`

O Flyway roda as migrations automaticamente ao subir a aplicação.

## Estrutura de pastas

src/main/java/br/edu/unesc/gamehub/
├── entity/ # Entidades JPA
├── repository/ # Interfaces JpaRepository
├── dto/ # DTOs de entrada e saída (nunca expor entity direto)
├── service/ # Regras de negócio
├── controller/ # Endpoints REST (usa @Valid nos DTOs de entrada)
├── config/ # Beans de configuração (PasswordEncoder, Security)
└── security/ # JWT e carregamento das autoridades do usuário

src/main/resources/
├── application.yml
└── db/migration/ # Migrations Flyway (uma por entidade, numeradas em sequência)


## Convenção para adicionar sua entidade

1. Migration em `db/migration` com o **próximo número disponível**
   (`V2__create_jogo_table.sql`, `V3__...`). Combinem no grupo quem usa qual
   número antes de dar push, pra não duplicar.
2. Seguir o mesmo padrão de pacotes do `usuario`: entity → repository → dto
   → service → controller.
3. Nunca expor `@Entity` direto no controller — sempre por DTO.

**Nota sobre migrations:** a migration de Compra foi publicada como `V6`, pois
`V5` já estava ocupada por Avaliacao. A migration V4 original não era
reconhecida pelo Flyway e o banco já tinha aplicado V5; usar V6 preserva a
ordem do histórico sem executar uma migration fora de ordem.

## Segurança e autorização

A API usa JWT stateless. `POST /api/usuarios` (cadastro),
`POST /api/auth/login` e Swagger/OpenAPI são públicos; os demais endpoints
exigem `Authorization: Bearer <token>`. Escritas de Jogo e Categoria exigem
perfil `ADMIN`.

Os CRUDs de Compra e Avaliacao já existem. Os responsáveis por essas entidades
ainda devem aplicar RN08/RN09: somente o dono do recurso ou um `ADMIN` pode
acessá-lo. Usem `@PreAuthorize` ou verifiquem o usuário autenticado via
`SecurityContextHolder`, seguindo o padrão de autorização usado em Jogo/Categoria.

## Exemplo de request/response (Usuario)

**POST /api/usuarios**
```json
{
  "nome": "Daniel Felisberto",
  "email": "usuario@gamehub.com",
  "senha": "senha123"
}
```

> Exemplo ilustrativo. Para testar o login, cadastre primeiro um usuário novo
> via `POST /api/usuarios` e use o mesmo email e senha em `POST /api/auth/login`.

**Resposta 201**
```json
{
  "id": 1,
  "nome": "Daniel Felisberto",
  "email": "daniel@gamehub.com",
  "perfil": "CLIENTE",
  "ativo": true,
  "dataCadastro": "2026-09-16T14:40:00"
}
```

## Credenciais (ambiente local)

| Serviço  | Usuário  | Senha       |
|----------|----------|-------------|
| Postgres | gamehub  | gamehub123  |

---

## 📋 Andamento do projeto

### ✅ Feito
- [x] Setup do projeto (pom.xml, application.yml, docker-compose, Flyway)
- [x] Entidade **Usuario** completa: entity, migration `V1`, repository, DTOs,
  validações, service (e-mail único, senha com hash BCrypt), controller
  REST com CRUD (Daniel) — testado e funcionando
- [x] Entidades **Jogo** e **Categoria**, CRUDs e relacionamento N:N
- [x] Entidade **Compra** (migration `V6`) e regras RN05 (compra duplicada) e RN06 (valor pago)
- [x] Entidade **Avaliacao** e regras RN09/RN10/RN11 (compra necessária, nota 1-5 e unicidade)
- [x] Tratamento centralizado de exceções (`@ControllerAdvice`)
- [x] Spring Security + JWT stateless, login e autorização ADMIN em Jogo/Categoria

### 🔄 Em andamento
- [ ] _(atualizar aqui conforme o grupo for pegando tarefas)_

### ⏳ Pendente
- [ ] Aplicar RN08/RN09 nos endpoints de Compra/Avaliacao: somente dono do recurso ou ADMIN
- [ ] Protótipo das telas (Figma)
- [ ] Documentação Swagger revisada
- [ ] Apresentação (pitch 10-15min)

## 📅 Prazos

| Etapa | Entrega | Data |
|---|---|---|
| Documento + DER | Planejamento e modelagem | 15/09/26 ✅ |
| CRUDs básicos | Entidades e relacionamentos | 22/09/26 ✅ |
| JWT + Spring Security | Segurança e refino | 29/09/26 |
| Aplicação das camadas | Controller, Services, Repository | 06/10/26 |
| Refinamento da solução | Swagger + ajustes | 20/10/26 |
| Projeto final + apresentação | — | 27/10/26 |