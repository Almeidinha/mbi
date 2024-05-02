FROM maven:3.8.3-openjdk-17

LABEL mentainer="blackheal@gmail.com"

WORKDIR /mbi
COPY . .

RUN mvn clean install -DskipTests

ENTRYPOINT ["java","-jar","/mbi/mbi-web/target/mbi-web-0.0.1-SNAPSHOT.jar"]
