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

No Swagger, faça login em `POST /api/auth/login`, copie o valor do token
retornado e cole no botão **Authorize**, sem escrever `Bearer`.

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

A compra ainda precisa da RN08: somente o dono da compra ou um `ADMIN` pode
acessá-la. Em Avaliacao, o autor vem do token; somente quem comprou o jogo
pode avaliar e somente o autor pode atualizar ou excluir (RN09/RF13).

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
| Admin de desenvolvimento | admin@gamehub.com | Admin@2026 |

A conta admin e somente para ambiente local e demonstração.

---

## 📋 Andamento do projeto

### ✅ Feito
- [x] Setup do projeto (pom.xml, application.yml, docker-compose, Flyway)
- [x] Entidade **Usuario** completa: entity, migration `V1`, repository, DTOs,
  validações, service (e-mail único, senha com hash BCrypt), controller REST
  com CRUD (Daniel) — testado e funcionando
- [x] Entidades **Jogo** e **Categoria**, CRUDs e relacionamento N:N
- [x] Entidade **Compra** (migration `V6`) e regras RN05 (compra duplicada) e RN06 (valor pago)
- [x] Entidade **Avaliacao**, RN09 (compra necessária e autor pelo token),
  RN10/RN11 e RF13 (somente autor atualiza/exclui)
- [x] Spring Security + JWT stateless, login e autorização ADMIN em Jogo/Categoria
- [x] Swagger global e documentação de Auth, Usuario e Avaliacao
- [x] Administrador inicial para desenvolvimento/demonstração (migration `V7`)
- [x] Segredo JWT configurável por `JWT_SECRET`, com fallback local
- [x] Tratamento centralizado de exceções (@ControllerAdvice) - parcial: handler incompleto: falta exceções de Avaliação, @Valid, 403 e fallback 500 (Igor)
- [x] Documentar CategoriaController e DTOs de Categoria; completar GlobalExceptionHandler (Igor)
- [x] Padronizar a resposta HTTP 403 de Avaliacao no GlobalExceptionHandler
- [x] Documentar JogoController e DTOs de Jogo no Swagger (Gustavo)

### 🔄 Em andamento
- [ ] _(atualizar aqui conforme o grupo for pegando tarefas)_

### ⏳ Pendente
- [ ] Documentar CompraController e DTOs de Compra; implementar RN08 (Lucas)
- [ ] Protótipo das 10 telas no Figma (cada um as suas) para 27/10
- [ ] Revisão final do README para 27/10
- [ ] Apresentação (pitch de 10-15 min) para 27/10
- [ ] Confirmar com o professor se o front funcional será exigido

## Divisão da etapa 20/10/26 (Swagger + ajustes)

> **REGRAS PARA QUEM FOR EXECUTAR (pessoa ou agente de IA):**
> 1. Faça somente as tarefas listadas na SUA linha da tabela.
> 2. Não edite arquivos que pertencem a outra pessoa.
> 3. Não crie funcionalidades, endpoints, entidades ou dependências novas.
> 4. Não altere migrations já existentes nem renomeie nada.
> 5. Faça `git pull origin main` antes de começar e commit pequeno ao terminar.
> 6. Ao concluir, marque a tarefa como feita neste README (só a sua linha).
> 7. Para documentar o Swagger, use apenas @Tag, @Operation, @ApiResponse e
>    @Schema(example=...). Não mude a lógica dos métodos ao documentar.
> 8. O OpenApiConfig e o login do admin já existem; não recrie.

| Integrante | Tarefas desta etapa | Arquivos que pode editar |
|---|---|---|
| Daniel | FEITO: Swagger global; docs de Auth, Usuário e Avaliação; admin inicial (migration); JWT secret por env; RN09/RF13 (autor vem do token; só o autor edita/exclui) | config/OpenApiConfig, AuthController, UsuarioController, AvaliacaoController, AvaliacaoService, DTOs de Auth/Usuario/Avaliacao, migration do admin, application.yml, README |
| Igor | FEITO: Documentar CategoriaController e DTOs de Categoria; completar o GlobalExceptionHandler (exceções de Avaliação, erros de @Valid/MethodArgumentNotValidException, 403 e fallback 500, mantendo o formato de erro atual) | CategoriaController, DTOs de Categoria, exception/ |
| Gustavo | FEITO: Documentar JogoController e DTOs de Jogo no Swagger | JogoController, DTOs de Jogo |
| Lucas | FEITO: Documentar CompraController e DTOs de Compra; implementar RN08 (só o dono da compra ou ADMIN acessa; usuário obtido pelo SecurityContextHolder, nunca pelo corpo da requisição) | CompraController, CompraService, DTOs de Compra |

## Frontend / Protótipo das telas (entrega 27/10)

> Regras: cada pessoa faz só as telas da sua linha. O protótipo é no Figma e
> não precisa ser funcional. A demonstração no pitch pode ser pelo Swagger.
> Front funcional só se o professor exigir (a confirmar).

| # | Tela | Responsável |
|---|---|---|
| 1 | Login | Implementado (Daniel) — `http://localhost:8080/index.html` |
| 2 | Cadastro de Usuário | Implementado (Daniel) — `http://localhost:8080/cadastro.html` |
| 6 | Meu Perfil | Implementado (Daniel) — `http://localhost:8080/perfil.html` |
| 10 | Gerenciar Usuários | Implementado (Daniel) — `http://localhost:8080/usuarios.html` |
| 3 | Home / Catálogo de Jogos | Gustavo |
| 4 | Detalhes do Jogo | Gustavo (avaliações dentro da tela: Implementado (Daniel) — `http://localhost:8080/avaliacoes.html?jogoId=ID`) |
| 8 | Gerenciar Jogos | Gustavo |
| 9 | Gerenciar Categorias | Igor |
| - | Filtros de categoria da Home, navbar e componentes compartilhados | Igor |
| 5 | Minha Biblioteca / Compras | Lucas |
| - | Fluxo de compra dentro de Detalhes do Jogo | Lucas |
| 7 | Painel Administrativo | Lucas |

### Front funcional

Acesse o front em `http://localhost:8080/index.html` depois de iniciar a aplicação Spring Boot.
As telas de login, cadastro, perfil, gerenciamento de usuários e avaliações usam HTML, CSS e JavaScript servidos pelo próprio Spring Boot.

**Rotas adicionadas para as telas:**
- `GET /api/usuarios/me` retorna o usuário autenticado, pois o JWT contém somente o e-mail no `sub`.
- `POST /api/usuarios/admin` e `PUT /api/usuarios/{id}/admin` permitem ao ADMIN criar e editar usuários, incluindo o perfil. Ambas validam o DTO administrativo.
- A listagem completa de usuários agora exige perfil ADMIN.

**Integrações pendentes das telas:**
- O link “Comprar jogo” leva ao catálogo (`/jogos`), cuja tela e fluxo de compra estão com os colegas.
- A elegibilidade para avaliar é consultada por meio de `GET /compras` e a API também valida a compra ao publicar (`POST /avaliacoes`).
- `GET /avaliacoes/jogo/{jogoId}` já existia e é usado pela tela de avaliações.
- Navbar: as novas telas de catálogo, biblioteca e painel administrativo também deverão utilizar o componente compartilhado. As telas de login e cadastro não utilizam essa navbar.

**Pendente para os colegas:** telas de Home/Catálogo e detalhes do jogo (Gustavo); filtros de categoria (Igor); Minha Biblioteca/Compras, fluxo de compra e Painel Administrativo (Lucas); protótipos restantes de jogos e categorias conforme divisão do grupo.
(o catálogo deverá utilizar as funções montarFiltroCategorias() e filtrarPorCategoria(). O componente consulta as categorias em GET /categorias e filtra os jogos pelo campo categorias retornado pela API.)

## 📅 Prazos

| Etapa | Entrega | Data |
|---|---|---|
| Documento + DER | Planejamento e modelagem | 15/09/26 ✅ |
| CRUDs básicos | Entidades e relacionamentos | 22/09/26 ✅ |
| JWT + Spring Security | Segurança e refino | 29/09/26 ✅ |
| Aplicação das camadas | Controller, Services, Repository | 06/10/26 ✅ |
| Refinamento da solução | Swagger + ajustes | 20/10/26 |
| Projeto final + apresentação | — | 27/10/26 |
