FROM maven:3.6.3-jdk-8-slim as builder
WORKDIR /usr/app

COPY . .

RUN mvn install

FROM openjdk:8-jdk-alpine3.9
WORKDIR /usr/app

COPY --from=builder /usr/app/parser-chains-config-service/target/parser-chains-config-service-1.0-SNAPSHOT.jar .

ENV PORT=3000

CMD java -jar /usr/app/parser-chains-config-service-1.0-SNAPSHOT.jar --server.port=${PORT} --config.path=/tmp/config
