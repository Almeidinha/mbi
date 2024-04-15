FROM maven:3.8.3-openjdk-17 AS build

LABEL mentainer="blackheal@gmail.com"

COPY mbi-web/src /home/app/src
COPY mbi-web/pom.xml /home/app
RUN mvn -f /home/app/pom.xml -Pprod clean install
EXPOSE 8080
ENTRYPOINT ["java","-jar","/home/app/target/mbi-web-0.0.1-SNAPSHOT"]
