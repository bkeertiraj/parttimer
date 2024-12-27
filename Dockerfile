# FROM eclipse-temurin:22-jre-alpine
# WORKDIR /app
# COPY target/*.jar app.jar
# ENTRYPOINT ["java","-jar","/app.jar"]
# EXPOSE 8080

# Use Maven to build the project
# FROM maven:3.9-eclipse-temurin-22 AS builder
# WORKDIR /app
#
# # Copy the project files to the container
# COPY . .
#
# # Build the project
# RUN mvn clean package -DskipTests
#
# # Use a smaller image for running the application
# FROM eclipse-temurin:22-jre-alpine
# WORKDIR /app
#
# # Copy the built JAR file from the previous stage
# COPY --from=builder /app/target/*.jar app.jar
#
# ENTRYPOINT ["java", "-jar", "/app.jar"]
# EXPOSE 8080


FROM eclipse-temurin:22-jre-alpine
WORKDIR /app
COPY target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]
EXPOSE 8080
