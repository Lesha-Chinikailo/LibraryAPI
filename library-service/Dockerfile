FROM openjdk:22-jdk-slim
EXPOSE 8082
COPY build/libs/library-service-0.0.1-SNAPSHOT.jar .
ENTRYPOINT ["java", "-jar", "library-service-0.0.1-SNAPSHOT.jar"]