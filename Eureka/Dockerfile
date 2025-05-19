# Stage 1: Build the application
FROM eclipse-temurin:17-jdk-jammy AS builder

WORKDIR /app

COPY pom.xml .
COPY .mvn .mvn
COPY mvnw mvnw
RUN chmod +x mvnw
ENV MAVEN_CONFIG=""
RUN ./mvnw dependency:go-offline

COPY src ./src

RUN ./mvnw package -DskipTests

# Stage 2: Run the application
FROM amazoncorretto:17

WORKDIR /app

COPY --from=builder /app/target/*.jar app.jar

EXPOSE 7074

ENTRYPOINT ["java","-jar","app.jar"]