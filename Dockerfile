FROM gradle:8.14.3-jdk17 AS build
WORKDIR /app
COPY gradle gradle
COPY build.gradle* settings.gradle* ./
RUN gradle dependencies

COPY . .
RUN gradle buildFatJar --no-daemon

FROM eclipse-temurin:17-jre
WORKDIR /app


ENV DB_HOST=localhost \
    DB_URL=jdbc:postgresql://localhost:5432/wist \
    DB_NAME=shrtkt \
    DB_USER=postgres \
    DB_PASSWORD=password\
    PORT=8080

COPY --from=build /app/server/build/libs/*-all.jar app.jar

EXPOSE $PORT

CMD ["sh", "-c", "java \
  -Ddb.host=$DB_HOST \
  -Ddb.url=$DB_URL \
  -Ddb.name=$DB_NAME \
  -Ddb.user=$DB_USER \
  -Ddb.password=$DB_PASSWORD \
  -jar app.jar"]
