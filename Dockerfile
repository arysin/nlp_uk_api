#FROM openjdk:17-alpine

FROM ubuntu:20.04

RUN apt update
RUN apt install -y openjdk-17-jdk-headless

COPY gradlew settings.gradle /opt/nlp_uk_api/
COPY gradle/ /opt/nlp_uk_api/gradle
COPY app/src /opt/nlp_uk_api/app/src
COPY app/build.gradle /opt/nlp_uk_api/app

RUN cd /opt/nlp_uk_api/ && /bin/sh ./gradlew --no-daemon build

EXPOSE 8080
WORKDIR /opt/nlp_uk_api
CMD /bin/sh ./gradlew -q --no-daemon run 2>&1 > run.log
