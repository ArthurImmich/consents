Desenvolvimento seguindo a abordagem **TDD (Test-Driven Development)**, garantindo que cada parte do código fosse testável e que os requisitos fossem atendidos.

---

### Etapas:

1. **Testes e estrutura mínima**:
    - Criada a estrutura mínima e classes vazias (`controller`, `service`, `repository`, `dto`, `model`, `exception`, etc.) para garantir:
        - Uma abordagem TDD (Test Driven Development)
        - Nenhum erro inicial de IntelliSense
        - Separação de responsabilidades
        - Criação dos testes de integração
        - Migração de Spring MVC para Spring WebFlux conforme requisitos do cargo
    - **Testes de Integração**:
        - `POST /consents` (`201 Created`)
        - `GET /consents/{id}` (sucesso e falha 404)
        - `GET /consents` (com paginação)
        - `PUT /consents/{id}`
        - `DELETE /consents/{id}` (revogação)

...

2.  **Escrevendo o Primeiro Teste (Falho) - Teste de Integração**:
    *   Conforme o TDD, o primeiro passo foi escrever um teste de integração (`ConsentControllerIntegrationTest`) para o endpoint principal (`POST /consents`).
    *   Este teste definiu o "contrato" da API: o que ela deveria receber (um `JSON` com CPF), como deveria responder (HTTP `201 Created`, com um `JSON` contendo o consentimento criado) e quais validações deveriam ser aplicadas (CPF válido).
    *   Este teste falhou, como esperado, pois nenhuma implementação existia.

3.  **Implementação Mínima para o Teste Passar**:
    *   Em seguida, foi criado o código mínimo necessário para fazer o teste passar. Isso incluiu:
        *   `IConsentApi` e `ConsentController` para o endpoint.
        *   `CreateConsentRequestDTO` e `ConsentResponseDTO` para os dados de entrada e saída.
        *   `ConsentService` para a lógica de negócio (inicialmente vazia).
        *   `Consent` (modelo) e `ConsentRepository` para a persistência.
        *   `CpfValidator` para a validação customizada.
        *   `GlobalExceptionHandler` para o tratamento de erros de validação.


5.  **Adição de Testes Unitários**:
    *   Para garantir a lógica de negócio de forma isolada e rápida, foram criados testes unitários para a `ConsentService`.
    *   Utilizando `Mockito`, as dependências (como o `ConsentRepository`) foram mockadas, permitindo testar apenas a lógica da classe de serviço.

6.  **Implementação dos Requisitos Bônus**:
    *   As funcionalidades bônus também seguiram o fluxo TDD:
        *   **Histórico de Alterações**: Foi criado o modelo `ConsentLog` e seu repositório. Os testes de integração foram atualizados para verificar se, após uma operação de escrita (POST, PUT, DELETE), um log correspondente era criado.
        *   **Documentação (Swagger)**: A dependência foi adicionada e a interface `IConsentApi` foi anotada para gerar a documentação.
        *   **Docker**: O `Dockerfile` e o `docker-compose.yml` foram criados para facilitar a execução do ambiente.

7.  **Refatoração e Documentação Final**:
    *   Ao longo de todo o processo, o código foi constantemente refatorado para melhorar a legibilidade e a manutenção.
    *   Finalmente, este `README.md` foi escrito para documentar o projeto, suas funcionalidades e como utilizá-lo.
