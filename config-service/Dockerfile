FROM openjdk:22-jdk-slim
EXPOSE 8001
RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*
COPY build/libs/config-service-0.0.1-SNAPSHOT.jar .
#RUN apt-get update && apt-get install -y curl
#RUN apt-get update && apt-get install -y \
#curl
#CMD /bin/bash
ENTRYPOINT ["java", "-jar", "config-service-0.0.1-SNAPSHOT.jar"]