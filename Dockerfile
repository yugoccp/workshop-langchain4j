FROM eclipse-temurin:21-jdk

EXPOSE 8080

WORKDIR /workspace

COPY pom.xml .
COPY .mvn .mvn
COPY --chmod=0755 mvnw .

RUN sed -i 's/\r$//' ./mvnw
RUN ./mvnw install

CMD ["tail", "-f", "/dev/null"]
