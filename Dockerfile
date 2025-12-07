#FROM eclipse-temurin:21-jre
#WORKDIR /app
#
## clean and package, creates jar file in target folder
#COPY target/moneymanager-0.0.1-SNAPSHOT.jar moneymanager-v1.0.jar
#
#EXPOSE 9090
#
#ENTRYPOINT ["java", "-jar", "moneymanager-v1.0.jar"]


# Stage 1: Build the application
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Run the application
FROM eclipse-temurin:17-jdk
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 9090
ENTRYPOINT ["java", "-jar", "app.jar"]

