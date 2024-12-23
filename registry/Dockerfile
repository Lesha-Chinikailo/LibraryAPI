FROM openjdk:22-jdk-slim
EXPOSE 8761
#RUN apt-get update && apt-get install -y bash
RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*
COPY build/libs/registry-0.0.1-SNAPSHOT.jar .
#RUN apk add --no-cache bash
#COPY wait-for.sh /wait-for.sh
#RUN chmod +x /wait-for.sh
ENTRYPOINT ["java", "-jar", "registry-0.0.1-SNAPSHOT.jar"]
#"/wait-for.sh", "config-service:8001", "--timeout=110", "--",