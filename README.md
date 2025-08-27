# Checkin Service

## General Information

Checkin Service is a backend application designed to manage user check-ins, authentication, wallet transactions, and related configurations. It provides RESTful APIs for user management, check-in tracking, and payment operations, making it suitable for gamified platforms, loyalty programs, or attendance systems.

### Project Structure

- **src/**: Main source code, including controllers, services, entities, repositories, configuration, and utilities.
- **deploy/**: Deployment resources, including Dockerfile, docker-compose.yml, and scripts for containerized deployment.
- **doc/**: Documentation and assets.
- **pom.xml**: Maven build configuration.
- **README.md**: Project documentation (this file).

### Features
- Global User Support: The service supports users from any time zone. By sending the X-Timezone header with each API request, clients ensure that all time-based operations (such as check-in windows, transaction timestamps, and reporting) are processed in the user’s local time zone. If the header is not provided or is invalid, the system defaults to UTC or a configured fallback.
- User authentication and role-based authorization
- Daily check-in tracking and configuration
- Wallet and transaction management
- RESTful API endpoints for all major operations
- Exception handling and validation
- Redis integration (via Redisson) - distributed lock to prevent race conditions and duplicate processing.
- Dockerized deployment support (development purpose only)

### Technology Stack

- **Java 17+**
- **Spring Boot** (REST API, Security, Data JPA)
- **Maven** (build tool)
- **Redis** (caching, distributed lock, via Redisson)
- **Docker** (containerization)
- **Liquibase** (database migrations)

## Build Instructions

### Prerequisites

- Java 17 or higher
- Maven 3.8+
- Git
- Docker & Docker Compose (for containerized deployment)

### Clone the Repository

```sh
git clone <repository-url>
cd checkin-service
```

### Build the Project

Build the application JAR (skip tests if you do not have a database configured):

```sh
mvn clean package -DskipTests
```

The built JAR will be located at `target/checkin-service-0.0.1-SNAPSHOT.jar`.

## Deployment Instructions

### Using Docker Compose

The `deploy/` directory contains all necessary files for containerized deployment.

#### Steps:

1. **Build and Start Services**
   Copy the built JAR to the deployment folder:
    ```sh
   cp target/checkin-service-0.0.1-SNAPSHOT.jar deploy/app
    ```
   From the project root:

   ```sh
   cd deploy
   docker-compose up -d
   ```

2. **Docker Compose Services**
    1. Postgres
       - image: postgres:17
       - Exposes port 5432
       - Environment variables for user, password, and database name
       - Data is persisted via a Docker volume
    2. Redis
       - image: redis:7
       - Exposes port 6379
       - Uses a custom config file at deploy/redis/redis.conf
       - Data is persisted via a Docker volume.
    3. Checkin Service
       - Built from the Dockerfile in deploy/app/
       - Exposes port 8081
       - Depends on Postgres and Redis services
       - Environment variables for database and Redis connection settings
    
3. **Environment Variables**

   Environment variables are defined in `deploy/docker-compose.yml` and include:
   - `SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME`, `SPRING_DATASOURCE_PASSWORD`: Database connection settings
   - `REDIS_ADDRESS`, `REDIS_PASSWORD`, `REDIS_DATABASE`: Redis connection settings

   Adjust these as needed for your environment.

4. **Check Logs & Verify**

   To view logs:
   ```sh
   docker-compose logs -f
   ```
   The application should be accessible at `http://localhost:8081` after startup. Check the [guideline.md](doc/guideline.md) file for more details about the API endpoints.