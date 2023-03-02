# Use a Java base image
FROM openjdk:17-jdk-slim

# Set the working directory in the container
WORKDIR /app

# Copy the Spring Boot application JAR file into the container
COPY build/libs/*.jar app.jar

# Install dependencies needed for MariaDB
RUN apt-get update && \
    apt-get install -y --no-install-recommends mariadb-client

# Expose port 8080 for the Spring Boot application
EXPOSE 8080

# Set environment variables for the Spring Boot application
ENV SPRING_DATASOURCE_URL=jdbc:mariadb://db_host/mySite
ENV SPRING_DATASOURCE_USERNAME=admin
ENV SPRING_DATASOURCE_PASSWORD=admin

# Start the Spring Boot application when the container starts
CMD ["java", "-jar", "app.jar"]