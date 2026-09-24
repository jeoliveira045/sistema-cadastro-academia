# ---------- Estágio 1: build ----------
FROM maven:3.9-eclipse-temurin-21 AS build

WORKDIR /app

# Copia primeiro só o pom.xml para aproveitar cache de dependências
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Agora copia o resto do código e builda
COPY src ./src
RUN mvn clean package -DskipTests

# ---------- Estágio 2: runtime (só JRE) ----------
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Aqui está o pulo do gato: copia o .jar do estágio "build"
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]