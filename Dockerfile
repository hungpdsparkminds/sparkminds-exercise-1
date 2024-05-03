FROM eclipse-temurin:21-jre-alpine
ADD target/library-management*.jar app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]