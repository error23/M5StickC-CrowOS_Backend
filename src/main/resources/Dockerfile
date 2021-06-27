FROM error23/spring-boot

# Set environnement variables
ENV JAVA_ARGS="-Dspring.profiles.active=PROD"
ENV DB_NAME=esp32

COPY SQL/@project.version@ sql
COPY @project.build.finalName@.jar bin
