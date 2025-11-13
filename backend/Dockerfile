FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /builder

ARG JAR_FILE=target/*.jar
COPY . ./

RUN --mount=type=cache,target=/root/.m2 \
    ./mvnw --no-transfer-progress \
      -f pom.xml clean package -DskipTests

RUN cp $JAR_FILE app.jar && \
    java -Djarmode=tools -jar app.jar extract \
      --layers --launcher --destination extracted

FROM eclipse-temurin:25-jre-alpine
WORKDIR /app

EXPOSE 8080
ENTRYPOINT ["java", "org.springframework.boot.loader.launch.JarLauncher"]

COPY --from=build /builder/extracted/dependencies/ ./
COPY --from=build /builder/extracted/spring-boot-loader/ ./
COPY --from=build /builder/extracted/snapshot-dependencies/ ./
COPY --from=build /builder/extracted/application/ ./
