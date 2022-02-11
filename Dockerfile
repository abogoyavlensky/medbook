FROM abogoyavlensky/clojure-openjdk11-build:0.2.0 AS build

WORKDIR /app
COPY . /app
RUN make build


FROM openjdk:11-slim-buster

WORKDIR /app
COPY --from=build /app/medbook.standalone.jar /app/medbook.standalone.jar
EXPOSE 8000
CMD ["java", "-jar", "medbook.standalone.jar"]
