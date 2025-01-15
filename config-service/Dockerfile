FROM openjdk:22-jdk-slim
EXPOSE 8001
RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*
COPY build/libs/config-service-0.0.1-SNAPSHOT.jar .
ENTRYPOINT ["java", "-jar", "config-service-0.0.1-SNAPSHOT.jar"]