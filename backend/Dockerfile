# Koristi Java 21
FROM eclipse-temurin:21-jdk-alpine

# Radni direktorij u containeru
WORKDIR /app

# Kopiraj Maven wrapper i pom.xml
COPY mvnw pom.xml ./
COPY .mvn .mvn

# Preuzmi dependencies offline
RUN ./mvnw dependency:go-offline -B

# Kopiraj cijeli kod
COPY src src

# Buildaj aplikaciju
RUN ./mvnw clean package -DskipTests

# Otvori port 8080
EXPOSE 8080

# Start komanda
CMD ["java", "-jar", "target/susjedi-0.0.1-SNAPSHOT.jar"]
