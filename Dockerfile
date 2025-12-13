# STAGE 1: Build the Application
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app

# Copy the pom.xml and download dependencies
# We use the global 'mvn' command here which is more reliable on Windows than './mvnw'
COPY pom.xml .
RUN mvn dependency:go-offline

# Copy the source code
COPY src ./src

# Build the application (skipping tests for speed)
RUN mvn clean package -DskipTests

# DEBUG: List the contents of the target folder to verify the JAR was created
# If this step fails, it means the build didn't work.
RUN ls -la /app/target

# STAGE 2: Run the Application
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

# Copy the JAR from the build stage
COPY --from=build /app/target/*.jar app.jar

# Expose port 5005 to match your application.properties
EXPOSE 5005

ENTRYPOINT ["java", "-jar", "app.jar"]