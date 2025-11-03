# ODM Platform Notification Server

The Open Data Mesh Platform's Notification service is a Spring Boot application that manages event subscriptions, event dispatching, and notification delivery to subscribed services.

## Overview
This service is part of the Open Data Mesh Platform initiative, providing notification capabilities across the platform, including:
- Managing subscriptions to event types
- Dispatching emitted events
- Delivering notifications to subscribers and tracking delivery status

## Core Functionalities
- **Subscription Management**: Create, list, update, and delete event subscriptions
- **Event Dispatching**: Accept and dispatch events to subscribers
- **Notification Tracking**: Persist notification attempts and statuses (DELIVERED, FAILED)

## Prerequisites
- Java 21
- Maven 3.6+
- PostgreSQL (production) or H2 (development)
- Docker (optional, for TestContainers and local DB)

## Setup Instructions

### 1. Database Configuration

PostgreSQL (Production)
```properties
# application.properties
spring.datasource.url=jdbc:postgresql://localhost:5432/notification
spring.datasource.username=your_username
spring.datasource.password=your_password
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
```

H2 (Development)
```properties
# application.properties
spring.datasource.url=jdbc:h2:mem:notification
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
```

### 2. Building the Project
```bash
git clone https://github.com/opendatamesh-initiative/odm-platform-pp-notification-server.git
cd odm-platform-pp-notification-server
mvn clean install
```

### 3. Running the Application

Local Development
```bash
mvn spring-boot:run
```

Run with specific profile
```bash
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

Docker Deployment
```bash
docker build -t odm-notification-server .

docker run -p 8080:8080 \
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://db:5432/notification \
  -e SPRING_DATASOURCE_USERNAME=your_username \
  -e SPRING_DATASOURCE_PASSWORD=your_password \
  odm-notification-server
```

## Configuration Options

### Application Properties
The application can be configured using `application.yml` or `application.properties`.

Server Configuration
```yaml
server:
  port: 8080
```

Spring Configuration
```yaml
spring:
  application:
    name: odm-platform-pp-notification-server
  banner:
    charset: UTF-8
    mode: console
```

Database Configuration
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/notification
    username: your_username
    password: your_password
  jpa:
    properties:
      hibernate:
        default_schema: ODM_NOTIFICATION
```

Flyway Database Migration
```yaml
spring:
  flyway:
    baselineOnMigrate: true
    locations: classpath:db/migration/postgresql
    schemas: public
    validateOnMigrate: false
    outOfOrder: true
```

ODM Platform Configuration
```yaml
odm:
  product-plane:
    notification-service:
      address: http://localhost:8080
      active: true
```

Logging Configuration
```yaml
logging:
  pattern:
    console: "%clr(%d{yyyy-MM-dd HH:mm:ss.SSS}){faint} %clr(%5p) %clr(${PID:- }){magenta} %clr(---){faint} %clr([%15.15t]){faint} %clr(%-40.40logger{39}){cyan} %clr(:){faint} %m%n%wEx"
    file: "%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n"
  level:
    org.springframework.web.filter.CommonsRequestLoggingFilter: DEBUG
```

These properties can be overridden using environment variables or command-line arguments.

### Docker Spring JSON Configuration
When running in Docker, you can pass configuration as a JSON string using `SPRING_PROPS`:

```bash
docker run -p 8080:8080 \
  -e SPRING_PROPS='{"spring":{"datasource":{"url":"jdbc:postgresql://db:5432/notification","username":"your_username","password":"your_password"},"jpa":{"properties":{"hibernate":{"default_schema":"ODM_NOTIFICATION"}}}},"odm":{"product-plane":{"notification-service":{"address":"http://notification-service:8080","active":true}}}}' \
  odm-notification-server
```

JSON mirrors the YAML hierarchy:

```json
{
  "spring": {
    "datasource": {
      "url": "jdbc:postgresql://db:5432/notification",
      "username": "your_username",
      "password": "your_password"
    },
    "jpa": {
      "properties": {
        "hibernate": {
          "default_schema": "ODM_NOTIFICATION"
        }
      }
    }
  },
  "odm": {
    "product-plane": {
      "notification-service": {
        "address": "http://notification-service:8080",
        "active": true
      }
    }
  }
}
```

### Environment Variables
- `SPRING_PROFILES_ACTIVE`: Active profile (dev, prod)
- `SPRING_DATASOURCE_URL`: DB URL
- `SPRING_DATASOURCE_USERNAME`: DB user
- `SPRING_DATASOURCE_PASSWORD`: DB password
- `SERVER_PORT`: App port
- `SERVER_SERVLET_CONTEXT_PATH`: API context path

## API Documentation
- Swagger UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI Spec: `http://localhost:8080/api-docs`

## API Endpoints (principal)
- `POST /api/v2/subscriptions`
- `GET /api/v2/subscriptions`
- `GET /api/v2/subscriptions/{uuid}`
- `PUT /api/v2/subscriptions/{uuid}`
- `DELETE /api/v2/subscriptions/{uuid}`
- `POST /api/v2/events/dispatch`
- `GET /api/v2/events`
- `GET /api/v2/events/{sequenceId}`
- `GET /api/v2/notifications`
- `GET /api/v2/notifications/{sequenceId}`

## Testing
Run the test suite:
```bash
mvn test
```
With coverage:
```bash
mvn test jacoco:report
```
Integration tests use TestContainers for PostgreSQL and may use WireMock to simulate external services.

## Contributing
1. Fork the repository
2. Create your feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## License
Apache License 2.0 – see the `LICENSE` file.

## Support
Open an issue in the GitHub repository.
