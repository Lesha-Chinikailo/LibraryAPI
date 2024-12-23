FROM openjdk:22-jdk-slim
EXPOSE 8989
#RUN apt-get update && apt-get install -y bash
COPY build/libs/gateway-0.0.1-SNAPSHOT.jar .
#COPY wait-for.sh /wait-for.sh
#RUN chmod +x /wait-for.sh
ENTRYPOINT ["java", "-jar", "gateway-0.0.1-SNAPSHOT.jar"]
#"/wait-for.sh", "registry:8761", "--timeout=90", "--",