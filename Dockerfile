# Stage 1: Build JAR using Maven + JDK 21
FROM maven:3.9.3-jdk-21 AS build

# Set working directory inside container
WORKDIR /app

# Copy pom.xml first for caching dependencies
COPY pom.xml ./

# Copy source code
COPY src ./src

# Build the JAR (skip tests)
RUN mvn clean package -DskipTests

# Stage 2: Use lightweight JRE to run the app
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Copy the JAR from the build stage
COPY --from=build /app/target/com.email.dm-0.0.1-SNAPSHOT.jar app.jar

# Expose the port your Spring Boot app runs on
EXPOSE 8080

# Run the JAR
ENTRYPOINT ["java","-jar","app.jar"]
