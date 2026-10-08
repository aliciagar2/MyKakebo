# Stage 1
FROM maven:3.9.11-eclipse-temurin-21-alpine AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# Stage 2
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar /mykakebo.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/mykakebo.jar"]