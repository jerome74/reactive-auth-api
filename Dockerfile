FROM openjdk:17.0.2-jdk-slim
LABEL maintainer="walter.longo@gmail.com"
VOLUME /tmp
EXPOSE 8080
ARG JAR_FILE=target/reactor-1.0.0.jar
ADD ${JAR_FILE} service.jar
ENTRYPOINT ["java","-Djasypt.encryptor.password=hope","-jar","/service.jar"]