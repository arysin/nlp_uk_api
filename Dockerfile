FROM openjdk:17-alpine

#RUN set -ex \
#  && apt-get update

ADD . app/ gradle/ settings.gradle gradlew
RUN chmod +x ./gradlew; /bin/sh ./gradlew build

EXPOSE 8080
WORKDIR /app
CMD /bin/sh ../gradlew -q --no-daemon run
