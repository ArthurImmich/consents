# 🚀 Desafio Técnico: API de Consentimentos

## 💡 Contexto
Sua missão é desenvolver uma API REST para gerenciar consentimentos de usuários para uso de dados. Cada consentimento pode ser criado, atualizado ou revogado. O objetivo é garantir um modelo de dados limpo, endpoints bem definidos e código testável.

## 📋 Requisitos Funcionais
Endpoints obrigatórios:
• POST /consents – Criar um novo consentimento
• GET /consents – Listar todos os consentimentos
• GET /consents/{id} – Buscar um consentimento por ID
• PUT /consents/{id} – Atualizar informações do consentimento
• DELETE /consents/{id} – Revogar (ou excluir) um consentimento

Atributos do Consentimento:
• id: UUID
• cpf: string (obrigatório, formato válido de CPF: ###.###.###-##)
• status: enum (ACTIVE, REVOKED, EXPIRED)
• creationDateTime: data/hora (gerada automaticamente)
• expirationDateTime: data/hora (opcional)
• additionalInfo: string (opcional, tamanho [max: 50, min: 1])

Histórico de alterações (opcional):
• Caso deseje, implemente uma forma de registrar alterações feitas nos consentimentos, ou seja, a cada chamada aos endpoints de PUT ou DELETE, um registro de rastreabilidade deverá ser gerado.

## 🛠️ Requisitos Técnicos
• Java 21
• Spring Boot (3.x preferencialmente)
• Maven
• JPA com qualquer banco de dados relacional ou MongoDB (sugerido)
• Bean Validation para validações
• DTOs + Lombok + Record + MapStruct
• Git com histórico de commits descritivos

## 🧪 Testes Requeridos
• Testes unitários com JUnit 5 e Mockito
• Testes de integração com Testcontainers (PostgreSQL, MongoDB, etc.)

## 🔍 O que vamos observar
• Estrutura de projeto (domain, dto, service, repository)
• Git com commits semânticos (ex: feat: criar endpoint POST de consentimento)
• Clareza nos testes e cobertura adequada
• Validação de CPF com Bean Validation
• Tratamento de erros com @ControllerAdvice
• Documentação com Swagger
• Histórico de alterações (bônus)
• Dockerfile/docker-compose (bônus)

## 📦 Entrega
1. Criar repositório público (ex: consent-api)
2. Incluir README.md com instruções e exemplos
3. Subir código com histórico representando o desenvolvimento
4. Compartilhar link do repositório

## 🌟 Bônus
• Histórico de alterações, item dos requisitos técnicos
• Implementar uma chamada externa para carregar o campo "additionalInfo". Pode ser para qualquer serviço externo, por exemplo: GET https://api.github.com/users/martinfowler
• Paginação do endpoint GET /consents – Listar todos os consentimentos
• Swagger/OpenAPI
• Dockerfile/docker-compose
• Exportação de métricas(prometheus)


## 🔗 Referências
• https://projectlombok.org/
• https://mapstruct.org/
• https://spring.io/projects/spring-boot
• https://start.spring.io/
• https://maven.apache.org/
