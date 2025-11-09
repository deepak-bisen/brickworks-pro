# STAGE 1: Build the application
# We use a base image that includes Maven and JDK 21
FROM maven:3.9.6-eclipse-temurin-21 AS build

# Set the working directory in the build stage
WORKDIR /app

# Copy the entire parent project and all its sub-modules (all services)
# The build context is now 'brickworks-pro-backend/brickworks-pro-parent'
COPY . .

# Change the working directory to the api-gateway service
WORKDIR /app/customer-service

# Run the Maven package command.
# This works because the parent pom.xml is now at /app/pom.xml
RUN mvn clean package -DskipTests

# STAGE 2: Create the final, lightweight runtime image
# Use the correct Eclipse Temurin JRE 21 slim image
FROM eclipse-temurin:21-jre-alpine

# Set the working directory for the final application
WORKDIR /app

# Copy the built JAR from the 'build' stage into this new image.
COPY --from=build /app/customer-service/target/*.jar app.jar

# Expose the port your application runs on.
EXPOSE 8080

# The command to run the application when the container starts.
ENTRYPOINT ["java", "-jar", "app.jar"]