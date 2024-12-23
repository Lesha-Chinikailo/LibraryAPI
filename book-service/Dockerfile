FROM openjdk:22-jdk-slim
EXPOSE 8081
#RUN apt-get update && apt-get install -y bash
COPY build/libs/book-service-0.0.1-SNAPSHOT.jar .
#COPY wait-for.sh /wait-for.sh
#RUN chmod +x /wait-for.sh
ENTRYPOINT ["java", "-jar", "book-service-0.0.1-SNAPSHOT.jar"]
#"/wait-for.sh", "gateway:8001", "--timeout=110", "--",