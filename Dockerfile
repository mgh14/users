FROM openjdk:17-alpine

WORKDIR /app
COPY "./target/users-0.0.1-SNAPSHOT.jar" "/app/app.jar"

EXPOSE 9000

CMD ["java", "-jar", "app.jar"]
