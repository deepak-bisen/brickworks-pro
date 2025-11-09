# STAGE 1: Build the application
# We use a base image that includes Maven and JDK 21
FROM maven:3.9.6-eclipse-temurin-21 AS build

# Set the working directory in the build stage
WORKDIR /app

# Copy the entire parent project and all its sub-modules (all services)
# The build context (set in Railway) is 'brickworks-pro-backend/brickworks-pro-parent'
COPY . .

# Run the Maven package command from the root.
# This is the correct way to build a single module in a monorepo.
# -pl : build only the 'customer-service' project
# -am : also build any dependencies of 'customer-service'
RUN mvn clean package -pl customer-service -am -DskipTests

# STAGE 2: Create the final, lightweight runtime image
# Use the correct Eclipse Temurin JRE 21 slim image
FROM eclipse-temurin:21-jre-alpine

# Set the working directory for the final application
WORKDIR /app

# Copy the built JAR from the 'build' stage into this new image.
# We know the path because we built the 'customer-service' module.
COPY --from=build /app/customer-service/target/*.jar app.jar

# Expose the port your application runs on.
# !!! IMPORTANT: Update this port to match your customer-service port (e.g., 8081) !!!
EXPOSE 8081

# The command to run the application when the container starts.
ENTRYPOINT ["java", "-jar", "app.jar"]