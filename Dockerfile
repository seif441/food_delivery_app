FROM eclipse-temurin:21-jdk

WORKDIR /app

COPY target/app-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 7860

ENTRYPOINT ["java", "-jar", "app.jar"]
