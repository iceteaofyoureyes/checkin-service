FROM eclipse-temurin:17-jdk
WORKDIR /app
RUN apt-get update && apt-get install -y netcat-openbsd bash
# Copy jar
COPY ./checkin-service-0.0.1-SNAPSHOT.jar app.jar

# Copy wait-for-it
COPY wait-for-it.sh wait-for-it.sh
RUN chmod +x wait-for-it.sh

ENTRYPOINT ["bash", "wait-for-it.sh", "postgres", "5432", "--", "bash", "wait-for-it.sh", "redis", "6379", "--", "java", "-jar", "app.jar"]