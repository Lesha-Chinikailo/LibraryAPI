FROM openjdk:22-jdk-slim
EXPOSE 8989
COPY build/libs/gateway-0.0.1-SNAPSHOT.jar .
ENTRYPOINT ["java", "-jar", "gateway-0.0.1-SNAPSHOT.jar"]