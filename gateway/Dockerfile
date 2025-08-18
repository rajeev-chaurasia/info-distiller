# --- Build Stage ---
# Use a full JDK image with Maven to build our application
FROM maven:3.9-eclipse-temurin-17 AS builder

WORKDIR /app

# Copy the Maven project file first to leverage Docker's build cache
COPY pom.xml .
# We don't need to copy the wrapper for the Docker build
# COPY .mvn/ .mvn
# COPY mvnw pom.xml ./

# Download all project dependencies using the container's built-in Maven
RUN mvn dependency:go-offline

# Copy the rest of the source code
COPY src ./src

# Build the application, creating the executable JAR file, using the container's Maven
RUN mvn package -DskipTests


# --- Run Stage ---
# Use a lightweight JRE image that is multi-platform compatible
FROM eclipse-temurin:17-jre

WORKDIR /app

# Copy the compiled JAR file from the builder stage
COPY --from=builder /app/target/*.jar app.jar

# Expose the port the application will run on
EXPOSE 8082

# The command to run the application when the container starts
ENTRYPOINT ["java", "-jar", "app.jar"]