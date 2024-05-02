FROM maven:3.8.3-openjdk-17 AS build

LABEL mentainer="blackheal@gmail.com"

COPY . /app

RUN mvn -f /app/pom.xml clean install -Pdocker -DskipTests

ENTRYPOINT ["java","-jar","/app/mbi-web/target/mbi-web-0.0.1-SNAPSHOT"]
