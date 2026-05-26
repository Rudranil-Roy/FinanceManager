FROM eclipse-temurin:21-jdk-alpine
VOLUME /tmp
COPY target/*.jar app.jar
ENTRYPOINT ["java", "-XX:MaxRAMPercentage=70.0", "-Xmx320m", "-jar", "/app.jar"]