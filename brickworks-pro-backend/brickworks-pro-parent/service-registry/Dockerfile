# STAGE 1: Build the application
# We use a base image that includes Maven and JDK 21
FROM maven:3.9.6-eclipse-temurin-21 AS build

# Set the working directory in the build stage
WORKDIR /app

# Copy the entire parent project.
# This ensures the parent pom.xml and all sibling modules (which might be
# dependencies) are available in the build context.
# Paths are relative to the repository root.
COPY brickworks-pro-backend/brickworks-pro-parent/ .

# Change the working directory to the specific service we want to build
WORKDIR /app/service-registry

# Run the Maven package command.
# Maven will automatically find the parent pom.xml in /app/
# This builds ONLY this module and its dependencies.
RUN mvn clean package -DskipTests

# STAGE 2: Create the final, lightweight runtime image
# We use a slim JRE (Java Runtime Environment) image, which is smaller
FROM openjdk:21-slim

# Set the working directory for the final application
WORKDIR /app

# --- This is the key fix ---
# Copy the built JAR from the 'build' stage into this new image.
# This command finds the wildcard *.jar (e.g., service-registry-0.0.1.jar)
# and renames it to a simple, predictable 'app.jar'.
COPY --from=build /app/service-registry/target/*.jar app.jar

# Expose the port your application runs on.
# For a Spring Cloud/Eureka service registry, the default is 8761.
# Change this if you have configured a different port.
EXPOSE 8761

# The command to run the application when the container starts.
# We use the 'exec' form (brackets) and the predictable 'app.jar' name.
# This avoids the wildcard issue that caused the crash.
ENTRYPOINT ["java", "-jar", "app.jar"]