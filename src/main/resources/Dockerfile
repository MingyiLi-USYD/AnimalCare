FROM openjdk:11 as build

VOLUME /tmp

ADD animalcare-0.0.1-SNAPSHOT.jar mingyi.jar

RUN bash -c 'touch /mingyi.jar'

ENTRYPOINT ["java","-jar","mingyi.jar"]

EXPOSE 8080