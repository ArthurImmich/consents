#  ---- BUILD ----
# Imagem completa para build com maven 3.9.6 e jdk 21
FROM maven:3.9.6-eclipse-temurin-21 AS builder

# Define o diretório de trabalho dentro do contâiner
WORKDIR /app

# Copia os arquivos necessários do maven wrapper
COPY mvnw .
COPY .mvn ./.mvn

# Copia o pom.xml
COPY pom.xml .

# Permite o mvn do maven wrapper ser executado dentro do container
RUN chmod +x mvnw

# Baixa as dependências sem compilar o projeto
RUN ./mvnw dependency:go-offline

# Copia o resto do projeto
COPY src ./src

# Compila o projeto
RUN ./mvnw clean package -DskipTests


# ---- CRIA IMAGEM -----
# Imagem mais leve e completa para gerar a imagem final
FROM eclipse-temurin:21-jre-alpine

# Define o diretório de trabalho dentro do contâiner
WORKDIR /app

# Cria um usuário sem acesso root e um grupo para rodar a aplicação por segurança
RUN addgroup -S spring && adduser -S spring -G spring

# Seleciona o usuário
USER spring

# Define o argumento para o .jar gerado
ARG JAR_FILE=target/*.jar

# Copia apenas a imagem da etapa de build para a imagem final
COPY --from=builder /app/${JAR_FILE} app.jar

# Expoe a porta
EXPOSE 8099

# Comando que executa a aplicação
ENTRYPOINT ["java", "-jar", "app.jar"]