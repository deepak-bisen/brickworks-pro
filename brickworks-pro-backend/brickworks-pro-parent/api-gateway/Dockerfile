# STAGE 1: Build the application
# We use a base image that includes Maven and JDK 21
FROM maven:3.9.6-eclipse-temurin-21 AS build

# Set the working directory in the build stage
WORKDIR /app

# Copy the entire parent project.
# This ensures the parent pom.xml and all sibling modules are available.
COPY brickworks-pro-backend/brickworks-pro-parent/ .

# Change the working directory to the api-gateway service
WORKDIR /app/api-gateway

# Run the Maven package command.
# This builds ONLY this module and its dependencies.
RUN mvn clean package -DskipTests

# STAGE 2: Create the final, lightweight runtime image
# Use the correct Eclipse Temurin JRE 21 slim image
# --- FIX: Using '21-jre-alpine' which is a valid slim JRE image ---
FROM eclipse-temurin:21-jre-alpine

# Set the working directory for the final application
WORKDIR /app

# Copy the built JAR from the 'build' stage into this new image.
# We find the *.jar and rename it to 'app.jar'.
COPY --from=build /app/api-gateway/target/*.jar app.jar

# Expose the port your application runs on.
# The previous Dockerfile screenshot showed 8080.
EXPOSE 8080

# The command to run the application when the container starts.
# This uses the predictable 'app.jar' name and avoids the wildcard error.
ENTRYPOINT ["java", "-jar", "app.jar"]