# --- Builder Stage ---
FROM eclipse-temurin:21 as builder
WORKDIR /opt/backend
COPY .mvn/ .mvn
COPY mvnw pom.xml ./
RUN ./mvnw dependency:go-offline
COPY ./src ./src
RUN ./mvnw clean install -DskipTests

FROM eclipse-temurin:21
WORKDIR /opt/backend
EXPOSE 8080

COPY --from=builder /opt/backend/target/backend-0.0.1-SNAPSHOT.jar .

ENTRYPOINT ["java","-jar","backend-0.0.1-SNAPSHOT.jar"]