FROM error23/spring-boot:2.0

# Metadata
LABEL author     = "error23"
LABEL email      = "error23.d@gmail.com"
LABEL version    = "@project.version@"
LABEL created    = "19/06/2021"
LABEL maintainer = "error32 <error23.d@gmail.com>"

# Set environnement variable
ENV SPRING_EXECUTABLE_JAR=@project.build.finalName@.jar
ENV JAVA_ARGS="-Dspring.profiles.active=PROD"
ENV SPRING_ARGS=""
ENV DB_NAME=esp32

# Copy pre and post deploy scripts
COPY preDeploy bin/preDeploy
COPY postDeploy bin/postDeploy

# Copy sql scripts to execute on server
COPY SQL sqlToExecute
RUN mv sqlToExecute/@project.version@/* sqlToExecute
RUN rm -fr sqlToExecute/@project.version@/

# Copy executable jar
COPY @project.build.finalName@.jar bin
