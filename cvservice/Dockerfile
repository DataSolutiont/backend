FROM maven:latest AS stage1
WORKDIR /cv_service
COPY pom.xml /cv_service
RUN mvn dependency:resolve
COPY . /cv_service
RUN mvn clean
RUN mvn package -DskipTests

FROM openjdk:21 AS final
COPY --from=stage1 /cv_service/target/*.jar app.jar
EXPOSE 8080
CMD ["java", "-jar", "app.jar"]
