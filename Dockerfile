FROM eclipse-temurin:22-jre-alpine
WORKDIR /app
COPY ./target/PartTimer-0.0.1-SNAPSHOT.jar /app/app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
EXPOSE 8080
