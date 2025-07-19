# 🚀 API de Consentimentos

Esta é uma API REST para gerenciamento de consentimentos de usuários, desenvolvida como solução para um desafio técnico. A API segue as melhores práticas de desenvolvimento de software, incluindo uma arquitetura limpa, testes abrangentes e utiliza um stack tecnológico moderno com Spring WebFlux, MongoDB e Docker.

## ✨ Funcionalidades

- **CRUD Completo**: Criação, leitura, atualização e revogação (exclusão) de consentimentos de forma reativa.
- **Validação de Dados**: Validação robusta de dados de entrada, incluindo formato de CPF, utilizando Bean Validation.
- **Paginação**: Suporte para paginação no endpoint de listagem de consentimentos.
- **Histórico de Alterações**: Rastreabilidade completa de todas as alterações (criação, atualização, revogação) de um consentimento, persistida em uma coleção separada para auditoria.
- **Chamada a Serviço Externo**: O campo `additionalInfo` é preenchido dinamicamente através de uma chamada a um serviço externo caso não seja fornecido na criação.
- **Documentação da API**: Documentação interativa e completa com Springdoc/OpenAPI (Swagger).
- **Containerização**: Suporte completo a Docker e Docker Compose para fácil configuração e execução do ambiente (API, Banco de Dados e Prometheus).
- **Métricas**: Exportação de métricas para o Prometheus através do Spring Boot Actuator.

## 🛠️ Tecnologias Utilizadas

- **Linguagem**: Java 21
- **Framework**: Spring Boot 3.x (com Spring WebFlux para programação reativa)
- **Banco de Dados**: MongoDB
- **Build Tool**: Maven
- **Testes**: JUnit 5, Mockito, Testcontainers
- **Bibliotecas**:
  - Lombok & MapStruct: Redução de boilerplate code e mapeamento de DTOs.
  - Springdoc OpenAPI: Geração de documentação da API.
  - Hibernate Validator: Validação de dados (incluindo CPF).
  - Spring Data MongoDB Reactive: Integração reativa com o banco de dados.
- **Containerização**: Docker, Docker Compose

## 🏗️ Etapas do Desenvolvimento (Abordagem TDD)

O desenvolvimento desta API seguiu uma abordagem **TDD (Test-Driven Development)**, garantindo que cada parte do código fosse testável e que os requisitos fossem atendidos de forma incremental e segura. O fluxo foi o seguinte:

1.  **Configuração e Estrutura do Projeto**:
    *   Definição das dependências no `pom.xml` (Spring WebFlux, MongoDB, Testcontainers, MapStruct, etc.).
    *   Criação da estrutura de pacotes (`controller`, `service`, `repository`, `dto`, `model`, `exception`, etc.) para garantir a separação de responsabilidades.

2.  **Escrevendo o Primeiro Teste (Falho) - Teste de Integração**:
    *   Conforme o TDD, o primeiro passo foi escrever um teste de integração (`ConsentControllerIntegrationTests`) para o endpoint principal (`POST /consents`).
    *   Este teste definiu o "contrato" da API: o que ela deveria receber, como deveria responder (HTTP `201 Created`) e quais validações deveriam ser aplicadas.
    *   Este teste falhou, como esperado, pois nenhuma implementação existia.

3.  **Implementação Mínima para o Teste Passar**:
    *   Em seguida, foi criado o código mínimo necessário para fazer o teste passar. Isso incluiu:
        *   `IConsentApiController` e `ConsentApiController` para o endpoint.
        *   DTOs (`ConsentRequestCreateDTO`, `ConsentResponseDTO`) para os dados.
        *   `ConsentService`, `Consent` (modelo) e `ConsentRepository` (reativo).
        *   `GlobalExceptionHandler` para o tratamento de erros de validação.

4.  **Repetindo o Ciclo para os Demais Endpoints**:
    *   O ciclo **"Escrever Teste -> Ver Falhar -> Implementar -> Ver Passar"** foi repetido para cada requisito funcional, utilizando `WebTestClient` para os testes de integração:
        *   `GET /consents/{id}` (sucesso e falha 404).
        *   `GET /consents` (com paginação).
        *   `PUT /consents/{id}`.
        *   `DELETE /consents/{id}` (revogação).

5.  **Adição de Testes Unitários**:
    *   Para garantir a lógica de negócio de forma isolada, foram criados testes unitários para a `ConsentService` utilizando `Mockito` para mockar dependências e `StepVerifier` para testar os fluxos reativos (`Mono` e `Flux`).

6.  **Implementação dos Requisitos Bônus**:
    *   As funcionalidades bônus também seguiram o fluxo TDD:
        *   **Histórico de Alterações**: Foi criado o modelo `ConsentLog` e seu repositório. Os testes de integração foram atualizados para verificar se, após uma operação de escrita, um log correspondente era criado.
        *   **Docker**: O `Dockerfile` e o `docker-compose.yml` foram criados para facilitar a execução do ambiente.

7.  **Refatoração e Documentação Final**:
    *   Ao longo do processo, o código foi constantemente refatorado para melhorar a legibilidade e a manutenção.
    *   Finalmente, este `README.md` foi escrito para documentar o projeto, suas funcionalidades e como utilizá-lo.

## 📋 Pré-requisitos

- **Java 21** (ou superior)
- **Maven 3.8** (ou superior)
- **Docker** e **Docker Compose**

## ⚙️ Como Executar a Aplicação

### 1. Usando Docker Compose (Recomendado)

Este é o método mais simples. Ele irá construir a imagem da API e iniciar os containers da aplicação, do MongoDB e do Prometheus.

1.  **Clone o repositório:**
    ```bash
    git clone https://github.com/ArthurImmich/consents.git
    cd consents
    ```

2.  **Inicie os containers:**
    ```bash
    docker-compose up --build
    ```

A API estará disponível em `http://localhost:8099`.

### 2. Manualmente (com Maven)

1.  **Inicie o MongoDB** (pode usar o `docker-compose.yml` para isso):
    ```bash
    docker-compose up -d mongodb
    ```
2.  **A aplicação irá se conectar ao MongoDB** usando o perfil `dev`, que já está configurado no arquivo `src/main/resources/application-dev.yml`.

3.  **Execute a aplicação:**
    ```bash
    ./mvnw spring-boot:run
    ```

A API estará disponível em `http://localhost:8099`.

## 🧪 Como Executar os Testes

Para executar a suíte completa de testes (unitários e de integração), utilize o comando:

```bash
./mvnw test
```

**Nota:** A execução dos testes de integração requer que o Docker esteja em execução, pois o Testcontainers será utilizado para provisionar um banco de dados MongoDB temporário.

## 📖 Documentação da API (Swagger)

Com a aplicação em execução, a documentação interativa da API estará disponível no seguinte endereço:

- **Swagger UI**: [http://localhost:8099/swagger-ui.html](http://localhost:8099/swagger-ui.html)
- **OpenAPI Spec (JSON)**: [http://localhost:8099/v3/api-docs](http://localhost:8099/v3/api-docs)

## 📊 Métricas (Prometheus)

Com o ambiente do `docker-compose` em execução:
- **Prometheus UI**: [http://localhost:9090](http://localhost:9090) (você pode executar queries como `http_server_requests_seconds_count`)
- **Endpoint de Métricas**: [http://localhost:8099/actuator/prometheus](http://localhost:8099/actuator/prometheus)

## 🌐 Exemplos de Uso (cURL)

Substitua `{CONSENT_ID}` pelo ID de um consentimento existente.

### Criar um consentimento
```bash
curl -X POST http://localhost:8099/api/v1/consents \
-H "Content-Type: application/json" \
-d '{
  "cpf": "012.345.678-90",
  "status": "ACTIVE",
  "expirationDateTime": "2028-12-31T23:59:59"
}'
```

### Buscar um consentimento por ID
```bash
curl -X GET http://localhost:8099/api/v1/consents/{CONSENT_ID}
```

### Listar todos os consentimentos (paginado)
```bash
curl -X GET "http://localhost:8099/api/v1/consents?page=0&size=5&sort=creationDateTime,desc"
```

### Atualizar um consentimento
```bash
curl -X PUT http://localhost:8099/api/v1/consents/{CONSENT_ID} \
-H "Content-Type: application/json" \
-d '{
  "status": "REVOKED"
}'
```

### Revogar (deletar) um consentimento
```bash
curl -X DELETE http://localhost:8099/api/v1/consents/{CONSENT_ID}
```