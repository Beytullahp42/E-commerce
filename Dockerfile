# Use Amazon Corretto 21 as the base image
FROM amazoncorretto:21

# Set the working directory in the container
WORKDIR /app

# Copy only the fat jar (exclude the plain jar)
COPY build/libs/E-Commerce-0.0.1-SNAPSHOT.jar app.jar

# Expose the app port
EXPOSE 8080

# Run the jar
ENTRYPOINT ["java", "-jar", "app.jar"]
