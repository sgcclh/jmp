FROM openjdk:11.0.1-slim-stretch

LABEL maintainer="dj.cass44@gmail.com"

WORKDIR /app
COPY . /app

# Build & package app
RUN ./gradlew shadowJar

EXPOSE 7000
VOLUME ["/data"]

ENTRYPOINT ["java", "-jar", "build/libs/jmp.jar"]
CMD ["using", "config.json"]