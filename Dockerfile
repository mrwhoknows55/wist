FROM eclipse-temurin:21-jre
WORKDIR /app

ENV DB_HOST=localhost \
    DB_URL=jdbc:postgresql://localhost:5432/wist \
    DB_NAME=shrtkt \
    DB_USER=postgres \
    DB_PASSWORD=password \
    PORT=8080

COPY server/build/libs/*-all.jar app.jar

EXPOSE $PORT

CMD ["sh", "-c", "java \
  -Ddb.host=$DB_HOST \
  -Ddb.url=$DB_URL \
  -Ddb.name=$DB_NAME \
  -Ddb.user=$DB_USER \
  -Ddb.password=$DB_PASSWORD \
  -jar app.jar"]
