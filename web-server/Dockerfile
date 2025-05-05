FROM maven:3.9.5-eclipse-temurin-17 AS builder
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

FROM openjdk:17-jdk-alpine
VOLUME /tmp
ARG JAVA_OPTS
ENV JAVA_OPTS=$JAVA_OPTS
COPY --from=builder /app/target/lockmng-0.0.1-SNAPSHOT.jar lockmng.jar
EXPOSE 8080
#ENTRYPOINT exec java $JAVA_OPTS -jar demo.jar
# For Spring-Boot project, use the entrypoint below to reduce Tomcat startup time.
ENTRYPOINT exec java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar lockmng.jar
