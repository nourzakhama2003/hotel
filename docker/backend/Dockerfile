FROM maven:3.8-openjdk-17 AS build
WORKDIR /build
COPY pom.xml .
RUN mvn dependency:go-offline
COPY src ./src
RUN mvn clean package -DskipTests

# Use Ubuntu-based image with GLIBC 2.27+ for OpenCV compatibility
FROM eclipse-temurin:17-jre-jammy AS runtime

# Install OpenCV dependencies
RUN apt-get update && apt-get install -y \
    libopencv-dev \
    python3-opencv \
    libgtk2.0-0 \
    libglib2.0-0 \
    libsm6 \
    libxext6 \
    libxrender-dev \
    libgomp1 \
    && rm -rf /var/lib/apt/lists/*

WORKDIR /app
COPY --from=build /build/target/hotel-1.0.0.jar /app/hotel-1.0.0.jar
# Create directory for face uploads
RUN mkdir -p /app/face-uploads
EXPOSE 8082
CMD ["java", "-jar", "/app/hotel-1.0.0.jar"]









# FROM maven:3-alpine
# WORKDIR /build as build
# COPY pom.xml .
# RUN mvn clean package -DskipTests
# COPY src ./src


# WORKDIR /app
# COPY --from=build /build/target/app.jar /app/app.jar
# EXPOSE 8082
# CMD java -jar /app/app.jar