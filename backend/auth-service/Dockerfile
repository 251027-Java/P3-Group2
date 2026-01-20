# syntax=docker/dockerfile:1

FROM eclipse-temurin:25-jdk-jammy AS deps

WORKDIR /build

COPY --chmod=0755 mvnw mvnw
COPY .mvn/ .mvn/

RUN --mount=type=bind,source=pom.xml,target=pom.xml \
    --mount=type=cache,target=/root/.m2 ./mvnw dependency:go-offline -DskipTests

FROM deps AS package

WORKDIR /build

COPY ./src src/
RUN --mount=type=bind,source=pom.xml,target=pom.xml \
    --mount=type=cache,target=/root/.m2 \
    ./mvnw package -DskipTests && \
    cp target/*.jar target/app.jar

FROM eclipse-temurin:25-jre-alpine AS final

COPY --from=package build/target/app.jar .

EXPOSE 8082

ENTRYPOINT [ "java", "-jar", "app.jar" ]
