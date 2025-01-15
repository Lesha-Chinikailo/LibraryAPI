FROM openjdk:22-jdk-slim
EXPOSE 8081
COPY build/libs/book-service-0.0.1-SNAPSHOT.jar .
ENTRYPOINT ["java", "-jar", "book-service-0.0.1-SNAPSHOT.jar"]