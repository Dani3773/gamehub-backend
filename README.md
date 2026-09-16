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
└── config/ # Beans de configuração (PasswordEncoder, Security)

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

## ⚠️ Sobre a Security (importante para o grupo)

O `pom.xml` já inclui `spring-boot-starter-security` (exigido pelo trabalho),
mas ainda **não tem JWT implementado**. Por padrão isso bloquearia todos os
endpoints com login HTTP Basic. Para poder testar os CRUDs agora, existe uma
`SecurityConfig` temporária em `config/` que libera tudo
(`permitAll()`). **Essa classe precisa ser substituída pela configuração real
de JWT** na etapa de Security (29/09) — não é definitiva.

## Exemplo de request/response (Usuario)

**POST /api/usuarios**
```json
{
  "nome": "Daniel Felisberto",
  "email": "daniel@gamehub.com",
  "senha": "senha123"
}
```

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

### 🔄 Em andamento
- [ ] _(atualizar aqui conforme o grupo for pegando tarefas)_

### ⏳ Pendente
- [ ] Entidade **Jogo**: entity, migration `V2`, repository, DTOs, service, controller (Gustavo)
- [ ] Entidade **Categoria**: entity, migration `V3`, repository, DTOs, service, controller (Igor)
- [ ] Relacionamento N:N Jogo ↔ Categoria (tabela `jogo_categoria`)
- [ ] Entidade **Compra**: entity, migration `V4`, repository, DTOs, service, controller (Lucas)
    - [ ] Regra RN05: impedir compra duplicada
    - [ ] Regra RN06: guardar valor pago no momento da compra
- [ ] Entidade **Avaliacao**: entity, migration `V5`, repository, DTOs, service, controller (Todos)
    - [ ] Regra RN09: só quem comprou pode avaliar
    - [ ] Regra RN10/RN11: nota 1-5, uma avaliação por usuário/jogo
- [ ] Tratamento centralizado de exceções (`@ControllerAdvice`)
- [ ] Spring Security + JWT real (substituir a `SecurityConfig` temporária)
- [ ] Protótipo das telas (Figma)
- [ ] Documentação Swagger revisada
- [ ] Apresentação (pitch 10-15min)

## 📅 Prazos

| Etapa | Entrega | Data |
|---|---|---|
| Documento + DER | Planejamento e modelagem | 15/09/26 ✅ |
| CRUDs básicos | Entidades e relacionamentos | 22/09/26 |
| JWT + Spring Security | Segurança e refino | 29/09/26 |
| Aplicação das camadas | Controller, Services, Repository | 06/10/26 |
| Refinamento da solução | Swagger + ajustes | 20/10/26 |
| Projeto final + apresentação | — | 27/10/26 |