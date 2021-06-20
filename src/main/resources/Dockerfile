FROM error23/spring-boot

ENV JAVA_ARGS="-Dspring.profiles.active=PROD"
ENV DB_NAME=esp32
ENV LANG=fr_FR.UTF-8

COPY SQL/@project.version@ sql
COPY @project.build.finalName@.jar bin
