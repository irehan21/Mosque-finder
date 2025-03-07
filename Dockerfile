FROM maven:3.8.5-openjdk-17 AS build
COPY . .
RUN mvn clean package -DskipTests

FROM maven:3.8.1-openjdk-17-slim
COPY --from=build /target/mosque-finder-0.0.1-SNAPSHOT.jar mosque-finder.jar
COPY .env .
EXPOSE 8080
ENTRYPOINT [ "java", "-jar", "mosque-finder.jar"]