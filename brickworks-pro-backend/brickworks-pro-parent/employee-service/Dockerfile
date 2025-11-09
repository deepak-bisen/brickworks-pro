# Use a base image with Java 21
FROM openjdk:21-slim

# Set a working directory
WORKDIR /app

# Copy the Maven wrapper
COPY .mvn/ .mvn
COPY mvnw .

# Copy the pom.xml and download dependencies
COPY pom.xml .
RUN ./mvnw dependency:go-offline

# Copy the rest of the source code and build the app
COPY src ./src
RUN ./mvnw clean package -DskipTests

# Expose the port (this is just for documentation)
# We will set the actual port with an environment variable
EXPOSE 8080

# Run the compiled .jar file
ENTRYPOINT ["java", "-jar", "target/*.jar"]