FROM maven:3.9-eclipse-temurin-25-alpine AS build
WORKDIR /app
COPY messaging-common/pom.xml messaging-common/pom.xml
COPY messaging-common/src messaging-common/src
RUN mvn -B -f messaging-common/pom.xml install \
    -Dmaven.test.skip=true \
    -Dspring-boot.repackage.skip=true
COPY web-api/pom.xml web-api/pom.xml
RUN mvn -B -f web-api/pom.xml dependency:go-offline
COPY web-api/src web-api/src
RUN mvn -f web-api/pom.xml clean package -Dmaven.test.skip=true

# ---
FROM eclipse-temurin:25-jre-alpine
WORKDIR /app
COPY --from=build /app/web-api/target/*.jar app.jar
CMD ["java", "-jar", "app.jar"]
