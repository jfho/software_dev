FROM eclipse-temurin:21 AS jre-build
WORKDIR /usr/src
COPY target/quarkus-app /usr/src/quarkus-app
 CMD ["sh", "-c", "sleep 5 && exec java -Xmx64m -jar quarkus-app/quarkus-run.jar"]