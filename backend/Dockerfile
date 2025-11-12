# Koristi Maven + Java 21
FROM maven:3.9.9-eclipse-temurin-21

WORKDIR /app

# Kopiraj cijeli backend
COPY backend ./

# Buildaj aplikaciju
RUN mvn clean package -DskipTests

EXPOSE 8080

CMD ["java", "-jar", "target/susjedi-0.0.1-SNAPSHOT.jar"]
