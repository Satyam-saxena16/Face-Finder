# =============================
# Render Deployment Info
# =============================
#
# 1. Connect your GitHub repo to Render.
# 2. Create a new Web Service and select this repo/branch.
# 3. Set the build command to:
#    ./mvnw package -DskipTests
# 4. Set the start command to:
#    java -jar app.jar
# 5. Set environment variables if needed (e.g., JAVA_OPTS, PYTHONPATH).
# 6. Expose port 8080 (default for Spring Boot).
#
# Uploaded images and results will be stored in /app/uploads inside the container.
#
# If you need persistent storage, use Render's Disk feature and mount it to /app/uploads.
# =============================

# Use an official OpenJDK image for Spring Boot
FROM openjdk:17-jdk-slim AS builder

# Install Python and pip
RUN apt-get update && apt-get install -y python3 python3-pip && rm -rf /var/lib/apt/lists/*

# Set working directory
WORKDIR /app

# Copy Maven wrapper and project files
COPY .mvn/ .mvn/
COPY mvnw mvnw
COPY mvnw.cmd mvnw.cmd
COPY pom.xml pom.xml
COPY src/ src/
COPY scripts/ scripts/
COPY uploads/ uploads/

# Install Python dependencies
RUN pip3 install insightface numpy pillow onnxruntime

# Build the Spring Boot application
RUN ./mvnw package -DskipTests

# Use a smaller OpenJDK image for running
FROM openjdk:17-jdk-slim
WORKDIR /app

# Copy built jar and resources
COPY --from=builder /app/target/*.jar app.jar
COPY --from=builder /app/scripts/ scripts/
COPY --from=builder /app/uploads/ uploads/

# Expose port 8080
EXPOSE 8080

# Run the Spring Boot application
ENTRYPOINT ["java", "-jar", "app.jar"]
