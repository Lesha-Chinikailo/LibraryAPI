FROM openjdk:22-jdk-slim
EXPOSE 8083
COPY build/libs/auth-service-0.0.1-SNAPSHOT.jar .
ENTRYPOINT ["java", "-jar", "auth-service-0.0.1-SNAPSHOT.jar"]