FROM maven:3.8.3-openjdk-17 AS build

LABEL mentainer="blackheal@gmail.com"

COPY mbi-cube /app/mbi-cube
COPY mbi-data /app/mbi-data
COPY mbi-model /app/mbi-model
COPY mbi-web /app/mbi-web
COPY mbi-parent.iml /app/mbi-parent.iml
COPY pom.xml /app/pom.xml

RUN mvn -f /app/pom.xml -Pprod clean install
EXPOSE 5000
ENTRYPOINT ["java","-jar","/app/mbi-web/target/mbi-web-0.0.1-SNAPSHOT"]

#FROM maven:3.8.5-openjdk-17
#
#WORKDIR /home/app/mbi
#COPY . .
#
#RUN mvn -Pprod clean install
#
#CMD java -jar /home/app/mbi/target/mbi-web/mbi-web-0.0.1-SNAPSHOT