# Pagamentos Spring

## Descrição

O Pagamentos Spring é um sistema de gerenciamento de contas a pagar, desenvolvido com Spring Boot. Ele permite criar,
atualizar, listar, pagar e deletar contas, além de gerenciar usuários. O sistema utiliza autenticação JWT para proteger
os endpoints e garantir que apenas usuários autenticados possam acessar as funcionalidades.

## Tecnologias Utilizadas

- Java 17
- Spring Boot 3.3.2
- Spring Security
- JWT
- JPA/Hibernate
- PostgreSQL (versão mais recente: postgres:latest)
- Flyway
- OpenAPI/Swagger
- Mockito (para testes unitários)

## Organização do Projeto

O projeto segue a abordagem DDD (Domain Driven Design) para organização das pastas e código.

## Configuração do Ambiente

### Pré-requisitos

- Docker e Docker Compose
- Java 17

### Comandos para rodar o Docker Compose

1. Compile e empacote a aplicação (sem a execução de testes):
    ```bash
    ./mvnw clean package -DskipTests
    ```

2. Construa a imagem Docker:
    ```bash
    docker build -t sergiobispo/pagamentos .
    ```

3. Suba os containers:
    ```bash
    docker-compose up
    ```

O sistema rodará na porta `localhost:8080`.

### Acessando a Aplicação

- Swagger UI: [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)

## Executando Testes

Para executar os testes unitários, utilize o seguinte comando:

```bash
./mvnw test
```

---

**OBS: É necessário estar com o docker rodando para executar os testes**

## Autenticação

Você precisará se autenticar no endpoint `/usuarios/login` e inserir o Bearer Token antes de consumir os endpoints.

### Usuários para a autenticação:

```
 E-mail's:
    - sergio.bispo@me.com;
    - maria.josefina@example.com;
    - jose.santos@example.com;
    
 Senha: 
    - senha123;
```

### Endpoints Públicos

- `/usuarios/login`
- `/usuarios` (POST)

## Banco de Dados

O Flyway irá popular o banco de dados PostgreSQL. Você pode encontrar os scripts em `resources/db/migration`.

### Exemplo de Script de Migração

```sql
INSERT INTO usuario (nome, email, senha, saldo)
VALUES ('Sergio Bispo', 'sergio.bispo@me.com', 'senha123', 1500.00),
       ('Maria Josefina', 'maria.josefina@example.com', 'senha123', 1500.00),
       ('José Santos', 'jose.santos@example.com', 'senha123', 500.00);
```

## Exemplo de Arquivo CSV

Para o endpoint de upload de CSV, você pode seguir este exemplo:

```
Nome,Descricao,Valor,DataVencimento,UsuarioId
"Conta de Luz","Conta mensal de energia elétrica",150.50,"15/09/2024",1
"Conta de Água","Conta mensal de água",75.25,"20/09/2024",1
"Internet","Serviço de internet mensal",99.99,"10/09/2024",2
"Aluguel","Pagamento mensal do aluguel",1200.00,"05/09/2024",3
"Telefone","Conta de telefone celular",49.90,"25/09/2024",1
```

Deixei um arquivo de exemplo em `resource/docs`.

## Estrutura de Pastas

```
.
├── Dockerfile
├── README.md
├── docker-compose.yml
├── mvnw
├── mvnw.cmd
├── pom.xml
├── src
│   ├── main
│   │   ├── java
│   │   │   └── com
│   │   │       └── sergiobispo
│   │   │           └── pagamentos
│   │   │               ├── PagamentosSpringApplication.java
│   │   │               ├── application
│   │   │               │   ├── controller
│   │   │               │   │   ├── ContaController.java
│   │   │               │   │   └── UsuarioController.java
│   │   │               │   ├── dto
│   │   │               │   │   ├── ContaDto.java
│   │   │               │   │   ├── LoginRequestDto.java
│   │   │               │   │   ├── LoginResponseDto.java
│   │   │               │   │   ├── MensagemDto.java
│   │   │               │   │   └── UsuarioDto.java
│   │   │               │   └── service
│   │   │               │       ├── ContaService.java
│   │   │               │       ├── CsvContaService.java
│   │   │               │       └── UsuarioService.java
│   │   │               ├── domain
│   │   │               │   ├── entities
│   │   │               │   │   ├── Conta.java
│   │   │               │   │   └── Usuario.java
│   │   │               │   └── enums
│   │   │               │       └── Situacao.java
│   │   │               ├── exception
│   │   │               │   ├── ContaJaPagaException.java
│   │   │               │   ├── CredenciaisInvalidasException.java
│   │   │               │   ├── GlobalExceptionHandler.java
│   │   │               │   ├── NotFoundException.java
│   │   │               │   ├── NullParameterException.java
│   │   │               │   ├── SaldoInsuficienteException.java
│   │   │               │   └── UsuarioSemPermissaoException.java
│   │   │               └── infrastructure
│   │   │                   ├── config
│   │   │                   │   ├── AppConfig.java
│   │   │                   │   ├── FlywayConfig.java
│   │   │                   │   ├── SecurityConfig.java
│   │   │                   │   └── SwaggerConfig.java
│   │   │                   ├── helpers
│   │   │                   │   └── JwtUtil.java
│   │   │                   ├── repository
│   │   │                   │   ├── ContaRepository.java
│   │   │                   │   └── UsuarioRepository.java
│   │   │                   └── security
│   │   │                       ├── JwtRequestFilter.java
│   │   │                       └── UserDetailsServiceImpl.java
│   │   └── resources
│   │       ├── application.properties
│   │       ├── db
│   │       │   └── migration
│   │       │       ├── V1__create_tables.sql
│   │       │       └── V2__insert_dados.sql
│   │       ├── docs
│   │       │   └── arquivo-contas.csv
│   │       ├── static
│   │       └── templates
│   └── test
│       └── java
│           └── com
│               └── sergiobispo
│                   └── pagamentos
│                       ├── PagamentosSpringApplicationTests.java
│                       └── application
│                           ├── controller
│                           │   ├── ContaControllerTest.java
│                           │   └── UsuarioControllerTest.java
│                           ├── dto
│                           │   ├── ContaDtoTest.java
│                           │   │   ├── MensagemDtoTest.java
│                           │   └── UsuarioDtoTest.java
│                           └── service
│                               ├── ContaServiceTest.java
│                               ├── CsvContaServiceTest.java
│                               └── UsuarioServiceTest.java

```
    



