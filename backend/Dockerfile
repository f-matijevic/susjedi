# Koristi Java 21
FROM eclipse-temurin:21-jdk-alpine

WORKDIR /app

# Kopiraj cijeli backend u container
COPY . .

# Preuzmi dependencies offline
RUN ./mvnw dependency:go-offline -B

# Buildaj aplikaciju
RUN ./mvnw clean package -DskipTests

EXPOSE 8080

CMD ["java", "-jar", "target/susjedi-0.0.1-SNAPSHOT.jar"]
