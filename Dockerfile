# Start with a base image containing Java runtime
FROM eclipse-temurin:21-jdk

# Set the working directory in the container
WORKDIR /workspace

COPY pom.xml .
COPY .mvn .mvn
COPY mvnw .

RUN sed -i 's/\r$//' ./mvnw
RUN ./mvnw install -DskipTests

# Keep the container running (alternatively, use an interactive shell)
CMD ["tail", "-f", "/dev/null"]
