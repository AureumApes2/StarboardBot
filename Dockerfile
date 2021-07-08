FROM openjdk:16-jdk-alpine3.13
COPY docker .
RUN ./gradlew build

FROM openjdk:16-jdk-alpine3.13
COPY --from=0 ./build/libs/StarboardBot-*.jar bot.jar
ENV TERM xterm-256color
ADD docker/start.sh start.sh

RUN chmod +x start.sh

ENTRYPOINT ["./start.sh"]