FROM 10.212.113.11:5000/openjdk-debian-13:21-slim

# Set the working directory for the appliocation
WORKDIR /app

# Copy the built JAR file from the build stage
COPY target/*.jar /app/app.jar

RUN chown -R  app_user:appgroup /app/

USER app_user

# Expose the port that the application will run on (if applicable)

EXPOSE 9434

# Command to run the JAR file
CMD ["java", "-jar", "app.jar"]
