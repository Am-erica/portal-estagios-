# portal-estagios-

Sistema de gerenciamento de vagas de estágio, desenvolvido em Java 17 com PostgreSQL.

## Integrantes
Camila Andrade Parolin RA: 10425480
Enzo de Paula Souza RA: 10752714
Erica Gonçalves de Oliveira RA: 10428547

## Banco de dados

Os scripts SQL do projeto estão no diretório `src/scripts/`:

- [`schema.sql`](src/scripts/schema.sql): cria as tabelas `usuarios`, `areas`, `vagas` e `candidaturas`, com chaves e restrições, e habilita RLS (não define políticas RLS).
- [`dados_teste.sql`](src/scripts/dados_teste.sql): insere dados fictícios para demonstração: uma estudante, uma empresa, um administrador, três áreas e uma vaga de estágio.

### Esquema das tabelas

| Tabela | Colunas e regras |
| --- | --- |
| `usuarios` | `id BIGINT` (PK, identity); `nome VARCHAR(120)` (obrigatório); `email VARCHAR(150)` (obrigatório e único); `perfil VARCHAR(20)` (obrigatório: `ESTUDANTE`, `EMPRESA` ou `ADMINISTRADOR`); `curso VARCHAR(120)` (opcional); `nome_empresa VARCHAR(150)` (opcional). |
| `areas` | `id BIGINT` (PK, identity); `nome VARCHAR(100)` (obrigatório e único). |
| `vagas` | `id BIGINT` (PK, identity); `empresa_id BIGINT` (FK para `usuarios.id`, obrigatório); `titulo VARCHAR(150)` (obrigatório); `descricao TEXT` (obrigatória); `area_id BIGINT` (FK para `areas.id`, obrigatório); `localizacao VARCHAR(150)` (obrigatória); `modalidade VARCHAR(20)` (obrigatória: `PRESENCIAL`, `REMOTA` ou `HIBRIDA`); `carga_horaria_semanal INTEGER` (obrigatória e maior que zero); `situacao VARCHAR(20)` (obrigatória, padrão `ABERTA`: `ABERTA` ou `ENCERRADA`). |
| `candidaturas` | `id BIGINT` (PK, identity); `estudante_id BIGINT` (FK para `usuarios.id`, obrigatório); `vaga_id BIGINT` (FK para `vagas.id`, obrigatório); `data_inscricao TIMESTAMP` (obrigatória, padrão `CURRENT_TIMESTAMP`); combinação `estudante_id` + `vaga_id` única. |

As quatro tabelas são criadas com RLS habilitado. O script atual não cria políticas de acesso RLS.

Execute primeiro o script de criação e, em seguida, o de dados de teste, em um banco PostgreSQL:

```bash
psql -h <host> -p 5432 -U <usuario> -d <banco> -f src/scripts/schema.sql
psql -h <host> -p 5432 -U <usuario> -d <banco> -f src/scripts/dados_teste.sql
```

Substitua `<host>`, `<usuario>` e `<banco>` pelos dados da sua instância. O `psql` solicitará a senha. Também é possível executar o conteúdo dos arquivos na mesma ordem pelo editor SQL do serviço PostgreSQL utilizado.

Para conectar a aplicação ao banco, configure as credenciais em `ConnectionFactory` (`src/main/java/dao/jdbc/ConnectionFactory.java`).

## Modelo de domínio

O domínio é composto por quatro classes principais:

- **Usuario**: `id`, `nome`, `email`, `perfil`, `curso` e `nomeEmpresa`. O perfil define se a conta representa um estudante, uma empresa ou um administrador; `curso` e `nomeEmpresa` são informações específicas dos respectivos perfis.
- **Area**: `id` e `nome`, usada para classificar as vagas.
- **Vaga**: `id`, empresa responsável, título, descrição, área, localização, modalidade, carga horária semanal e situação.
- **Candidatura**: `id`, estudante, vaga e data de inscrição. Representa a candidatura de um estudante a uma vaga.

Os tipos enumerados do domínio são `Perfil` (`ESTUDANTE`, `EMPRESA`, `ADMINISTRADOR`), `Modalidade` (`PRESENCIAL`, `REMOTA`, `HIBRIDA`) e `SituacaoVaga` (`ABERTA`, `ENCERRADA`). O administrador é um perfil de `Usuario`, não uma classe ou tabela separada.

Relacionamentos: uma empresa pode publicar várias vagas, e cada vaga pertence a uma empresa; uma área pode classificar várias vagas; um estudante pode realizar várias candidaturas, e cada candidatura pertence a um estudante; uma vaga pode receber várias candidaturas, e cada candidatura se refere a uma vaga. Assim, `Candidatura` relaciona estudantes e vagas, com no máximo uma candidatura por par estudante-vaga.

## Camadas do projeto

O código é organizado em pacotes, cada um com uma responsabilidade principal:

- **Apresentação (`app`)**: `Main` é o ponto de entrada da aplicação de console. Exibe menus, recebe as opções pelo terminal e aciona as operações correspondentes.
- **Serviços (`service`)**: implementam regras de negócio e validações. Por exemplo, `VagaService` valida os dados e verifica se o usuário tem perfil de empresa antes de publicar uma vaga.
- **Acesso a dados (`dao`)**: define interfaces como `VagaDAO`, que descrevem as operações de persistência sem concentrar nelas a implementação JDBC.
- **Persistência JDBC (`dao.jdbc`)**: implementa as interfaces DAO, executa comandos SQL no PostgreSQL e converte os resultados em objetos do domínio. `ConnectionFactory` fornece as conexões com o banco.
- **Domínio (`model`)**: contém as classes `Usuario`, `Area`, `Vaga` e `Candidatura`, além dos enums usados para representar perfis, modalidades e situações.

Em geral, o fluxo de uma operação é `app` → `service` → interface `dao` → implementação em `dao.jdbc` → PostgreSQL. No código atual, algumas ações do menu também consultam DAOs diretamente a partir de `Main`.

## Uso de inteligência artificial

A inteligência artificial foi utilizada como ferramenta de apoio durante o desenvolvimento nas seguintes atividades:

- **Código do UML**: auxílio na elaboração do diagrama de classes em PlantUML, incluindo classes do domínio, atributos, enums e relacionamentos, registrado em [`diagramaClasses.puml`](diagramaClasses.puml).
- **Scripts SQL**: auxílio na elaboração dos comandos de criação das tabelas e de inserção de dados fictícios, disponíveis em [`schema.sql`](src/scripts/schema.sql) e [`dados_teste.sql`](src/scripts/dados_teste.sql).
- **Validação das classes**: apoio à revisão das classes Java, verificando a coerência entre atributos, enums e relacionamentos representados no modelo de domínio, no UML e no esquema do banco.

A IA foi usada como suporte à análise e à produção dos artefatos; a conferência final deve considerar o código do projeto e a execução dos scripts e da aplicação.