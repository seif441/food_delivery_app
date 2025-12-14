# ---------- BUILD STAGE ----------
# We use a specific Maven image that matches your Java 21 version
FROM maven:3.9.9-eclipse-temurin-21 AS build

WORKDIR /app

# Copy your specific pom.xml and source code
COPY pom.xml .
COPY src ./src

# Build the application (skipping tests to speed it up)
RUN mvn clean package -DskipTests

# ---------- RUNTIME STAGE ----------
# Use the lightweight Java 21 image to run the app
FROM eclipse-temurin:21-jdk

WORKDIR /app

# The wildcard (*.jar) will automatically find 'app-0.0.1-SNAPSHOT.jar'
COPY --from=build /app/target/*.jar app.jar

# Expose the port your app runs on
EXPOSE 5005

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]