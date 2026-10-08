FROM maven:3.9-eclipse-temurin-25-alpine AS build
WORKDIR /app
COPY messaging-common/pom.xml messaging-common/pom.xml
COPY messaging-common/src messaging-common/src
RUN mvn -B -f messaging-common/pom.xml install \
    -Dmaven.test.skip=true \
    -Dspring-boot.repackage.skip=true
COPY stock-service/pom.xml stock-service/pom.xml
RUN mvn -B -f stock-service/pom.xml dependency:go-offline
COPY stock-service/src stock-service/src
RUN mvn -f stock-service/pom.xml clean package -Dmaven.test.skip=true

# ---
FROM eclipse-temurin:25-jre-alpine
WORKDIR /app
COPY --from=build /app/stock-service/target/*.jar app.jar
CMD ["java", "-jar", "app.jar"]
