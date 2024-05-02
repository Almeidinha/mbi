# Getting Started

### Reference Documentation
For further reference, please consider the following sections:

* [Official Apache Maven documentation](https://maven.apache.org/guides/index.html)
* [Spring Boot Maven Plugin Reference Guide](https://docs.spring.io/spring-boot/docs/2.6.7/maven-plugin/reference/html/)
* [Create an OCI image](https://docs.spring.io/spring-boot/docs/2.6.7/maven-plugin/reference/html/#build-image)



### Deploy locally
Make sure you hava Java and maven installed preferably java 17

Make sure you have a Mysql biserver:3306 instance running (or change the name in the application-prod.properties file)
Change the username:password to mach your instance (in application-prod.properties)

In the root dir ``(..\mbi)``, run ``mvn -Pprod clean install``

This will generate a jar file (mbi-web-0.0.1-SNAPSHOT) in  ``...\mbi\mbi-web\target``

You can run this jar with the command ``java -jar mbi-web-0.0.1-SNAPSHOT``

The app will run at ``localhost:5000``