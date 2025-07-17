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
