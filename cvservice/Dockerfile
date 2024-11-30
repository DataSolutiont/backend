FROM maven:latest AS stage1
WORKDIR /cv_service
COPY pom.xml /cv_service
RUN mvn dependency:resolve
COPY . /cv_service
RUN mvn clean
RUN mvn package -DskipTests

FROM openjdk:21 AS final
COPY --from=stage1 /cv_service/target/*.jar app.jar
RUN curl -o /wait-for-it.sh https://raw.githubusercontent.com/vishnubob/wait-for-it/master/wait-for-it.sh && \
    chmod +x /wait-for-it.sh
EXPOSE 8081
CMD ["./wait-for-it.sh", "cv-elsearch:9200", "--timeout=30", "--", "java", "-jar", "app.jar"]
# CMD ["java", "-jar", "app.jar"]
