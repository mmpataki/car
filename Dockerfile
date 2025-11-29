# lets first get dependencies
FROM amazoncorretto:8-al2023-jdk as deps
WORKDIR /build

RUN yum install -y tar

COPY --chmod=0755 mvnw mvnw
COPY .mvn/ .mvn/
COPY ./pom.xml ./pom.xml
COPY ./car-common/pom.xml ./car-common/pom.xml
COPY ./car-localfs/pom.xml ./car-localfs/pom.xml
COPY ./car-lucene-store/pom.xml ./car-lucene-store/pom.xml
COPY ./car-nativecluster-processor/pom.xml ./car-nativecluster-processor/pom.xml
COPY ./car-sdfs/pom.xml ./car-sdfs/pom.xml
COPY ./car-solr-store/pom.xml ./car-solr-store/pom.xml
COPY ./car-sql-store/pom.xml ./car-sql-store/pom.xml
COPY ./car-ui/pom.xml ./car-ui/pom.xml

RUN --mount=type=cache,target=/root/.m2 \
    ./mvnw dependency:go-offline -DskipTests


# build the frontend
FROM node:20 as frontended
WORKDIR /frontend-build

COPY car-ui/src/main/ui/package.json .
RUN npm install

COPY car-ui/src/main/ui/ .
RUN npm run build


# build the backend
FROM deps as package
WORKDIR /build

COPY . .
COPY --from=frontended /frontend-build/dist/ car-ui/target/classes/static/

RUN --mount=type=cache,target=/root/.m2 \
    ./mvnw install -DskipTests

RUN --mount=type=bind,source=pom.xml,target=pom.xml \
    --mount=type=cache,target=/root/.m2 \
    ./mvnw -pl car-ui package -DskipTests

RUN mkdir /build/target
RUN mv car-ui/target/car-ui-shaded.jar /build/target/app.jar

FROM amazoncorretto:8-al2023-jdk as final
ARG UID=10001
RUN adduser \
    --home "/nonexistent" \
    --shell "/sbin/nologin" \
    --no-create-home \
    --uid "${UID}" \
    appuser
USER appuser
COPY --from=package build/target/app.jar app.jar
COPY config.properties .

EXPOSE 8080
EXPOSE 9091
WORKDIR /appdir
ENTRYPOINT [ "java", "-jar", "/app.jar" ]
