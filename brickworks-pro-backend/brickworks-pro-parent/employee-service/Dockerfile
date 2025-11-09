# STAGE 1: Build the application
# We use a base image that includes Maven and JDK 21
FROM maven:3.9.6-eclipse-temurin-21 AS build

# Set the working directory in the build stage
WORKDIR /app

# Copy the entire parent project and all its sub-modules (all services)
# The build context is now 'brickworks-pro-backend/brickworks-pro-parent'
COPY . .

# Change the working directory to the api-gateway service
# --- FIX: We will stay at the parent /app directory ---
# WORKDIR /app/api-gateway

# Run the Maven package command.
# This works because the parent pom.xml is now at /app/pom.xml
# --- FIX: Use -pl to specify the project to build from the root ---
# This is a more robust way to build a single module in a monorepo.
# -pl : build only the 'api-gateway' project
# -am : also build any dependencies of 'api-gateway'
RUN mvn clean package -pl employee-service -am -DskipTests

# STAGE 2: Create the final, lightweight runtime image
# Use the correct Eclipse Temurin JRE 21 slim image
FROM eclipse-temurin:21-jre-alpine

# Set the working directory for the final application
WORKDIR /app

# Copy the built JAR from the 'build' stage into this new image.
COPY --from=build /app/employee-service/target/*.jar app.jar

# Expose the port your application runs on.
EXPOSE 8080

# The command to run the application when the container starts.
ENTRYPOINT ["java", "-jar", "app.jar"]