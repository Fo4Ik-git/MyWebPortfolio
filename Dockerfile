FROM openjdk:11-jdk

COPY target/app.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]

ENV DB_HOST=mariadb
ENV DB_PORT=3306
ENV DB_NAME=mySite
ENV DB_USER=admin
ENV DB_PASSWORD=admin

EXPOSE 8080