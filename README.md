# ODM Platform Notification Server

## Overview

The ODM Platform Notification Server is a microservice that provides event-driven notification capabilities for the Open
Data Mesh Platform. It acts as a central hub for managing event subscriptions and delivering notifications to registered
observers (subscribers).

<!-- TOC -->

* [ODM Platform Notification Server](#odm-platform-notification-server)
    * [Overview](#overview)
        * [Key Features](#key-features)
        * [How It Works](#how-it-works)
    * [Setup and Start](#setup-and-start)
        * [Prerequisites](#prerequisites)
        * [Configuration](#configuration)
            * [Environment Variables (Docker Profile)](#environment-variables-docker-profile)
        * [Running Locally](#running-locally)
            * [Option 1: Using Maven](#option-1-using-maven)
            * [Option 2: Using Docker](#option-2-using-docker)
            * [Option 3: Using the JAR](#option-3-using-the-jar)
        * [Default Port](#default-port)
        * [API Documentation](#api-documentation)
    * [Main Use Cases](#main-use-cases)
        * [1. Subscribe a New Observer](#1-subscribe-a-new-observer)
        * [2. Building a New Observer](#2-building-a-new-observer)
            * [Endpoint Paths](#endpoint-paths)
            * [API Signature - V2](#api-signature---v2)
            * [API Signature - V1](#api-signature---v1)
            * [Implementation Notes](#implementation-notes)
        * [3. Emit an Event](#3-emit-an-event)
        * [4. Replay a Notification](#4-replay-a-notification)
    * [Additional Endpoints](#additional-endpoints)
    * [Architecture](#architecture)
    * [License](#license)

<!-- TOC -->

### Key Features

- **Observer Subscription Management**: Register observers and subscribe them to specific event types
- **Event Emission**: Emit events that trigger notifications to subscribed observers
- **Asynchronous Notification Delivery**: Notifications are delivered asynchronously to observers via HTTP
- **Notification Replay**: Replay failed notifications to retry delivery
- **Event and Notification Tracking**: Full audit trail of events and notification delivery status

### How It Works

1. **Observers** (external services) register with the notification server and subscribe to specific event types
2. **Events** are emitted by ODM services (e.g., when a data product is created)
3. The notification server creates **notifications** for each subscribed observer
4. Notifications are **dispatched** asynchronously to observer endpoints via HTTP
5. Observers can acknowledge receipt and processing status of notifications
6. Failed notifications can be **replayed** to retry delivery

## Setup and Start

### Prerequisites

- Java 21 or higher
- Maven 3.6+
- PostgreSQL 12+ (for production) or H2 (for development)
- Docker (optional, for containerized deployment)

### Configuration

The service uses Spring Boot profiles for configuration:

- **`dev`**: Development profile with H2 in-memory database (default)
- **`docker`**: Docker profile requiring PostgreSQL environment variables
- **`test`**: Test profile for integration tests

#### Environment Variables (Docker Profile)

When running with the `docker` profile, configure:

```bash
DB_JDBC_URL=jdbc:postgresql://localhost:5432/odm_notification
DB_USERNAME=your_username
DB_PASSWORD=your_password
```

### Running Locally

#### Option 1: Using Maven

```bash
# Build the project
mvn clean install

# Run with default profile (dev - uses H2)
mvn spring-boot:run

# Run with specific profile
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

#### Option 2: Using Docker

```bash
# Build the Docker image
docker build -t odm-notification-server .

# Run the container
docker run -p 8080:8080 \
  -e DB_JDBC_URL=jdbc:postgresql://host.docker.internal:5432/odm_notification \
  -e DB_USERNAME=your_username \
  -e DB_PASSWORD=your_password \
  -e PROFILES_ACTIVE=docker \
  odm-notification-server
```

#### Option 3: Using the JAR

```bash
# Build the JAR
mvn clean package

# Run the JAR
java -jar target/odm-platform-pp-notification-server-0.0.1.jar

# Or with specific profile
java -jar -Dspring.profiles.active=dev target/odm-platform-pp-notification-server-0.0.1.jar
```

### Default Port

The service runs on port **8080** by default (configurable via `server.port` in `application.yml`).

### API Documentation

Once the service is running, access the Swagger UI at:

- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8080/v3/api-docs

## Main Use Cases

The service provides REST APIs (v2) for the following main use cases:

### 1. Subscribe a New Observer

Register an observer and subscribe it to specific event types.

**Endpoint**: `POST /api/v2/pp/notification/subscriptions/subscribe`

**Request Body**:

```json
{
  "name": "my-observer",
  "displayName": "My Observer Service",
  "observerBaseUrl": "https://observer.example.com",
  "observerApiVersion": "v1",
  "eventTypes": [
    "DATA_PRODUCT_CREATED",
    "DATA_PRODUCT_UPDATED"
  ]
}
```

**Response** (201 Created):

```json
{
  "subscription": {
    "uuid": "550e8400-e29b-41d4-a716-446655440000",
    "name": "my-observer",
    "displayName": "My Observer Service",
    "observerBaseUrl": "https://observer.example.com",
    "observerApiVersion": "v1",
    "eventTypes": [
      "DATA_PRODUCT_CREATED",
      "DATA_PRODUCT_UPDATED"
    ]
  }
}
```

**Notes**:

- If an observer with the same `name` already exists, it will be updated
- If `eventTypes` is provided, the observer will be subscribed to those types and unsubscribed from any types not in the
  list
- If `eventTypes` is empty or null, the observer will be subscribed to ALL events
- The observer must expose an endpoint at `{observerBaseUrl}/api/{observerApiVersion}/up/observer/notifications` to
  receive notifications

### 2. Building a New Observer

To build a new observer service that can receive notifications from the ODM Platform Notification Server, you need to:

1. **Expose a notification endpoint** that accepts POST requests
2. **Handle the notification payload** according to the API version you specified during subscription
3. **Return an appropriate HTTP status code** (2xx for success, 4xx/5xx for errors)

#### Endpoint Paths

The endpoint path depends on the API version you specified when subscribing:

- **V1**: `POST {observerBaseUrl}/api/v1/up/observer/notifications`
- **V2**: `POST {observerBaseUrl}/api/v2/up/observer/notifications`

#### API Signature - V2

For observers using API version `v2`, the notification server will send POST requests with the following payload
structure:

**Request Body**:

```json
{
  "sequenceId": 101,
  "event": {
    "sequenceId": 50,
    "resourceType": "DATA_PRODUCT",
    "resourceIdentifier": "d5b5b9ac-6a73-4c73-b9ce-4bfc10a1dba0",
    "type": "DATA_PRODUCT_CREATED",
    "eventTypeVersion": "2.0.0",
    "eventContent": {
      ...
    }
  },
  "subscription": {
    "name": "my-observer",
    "displayName": "My Observer Service",
    "observerBaseUrl": "https://observer.example.com",
    "observerApiVersion": "v2"
  }
}
```

**Response**: Return HTTP 200 OK (or any 2xx status) for successful processing. Return 4xx/5xx for errors.

#### API Signature - V1

For observers using API version `v1`, the notification server will send POST requests with the following payload
structure:

**Request Body**:

```json
{
  "id": 101,
  "event": {
    "id": 50,
    "type": "DATA_PRODUCT_CREATED",
    "entityId": "d5b5b9ac-6a73-4c73-b9ce-4bfc10a1dba0",
    "beforeState": {
      ...
    },
    "afterState": {
      ...
    },
    "time": "2024-01-15T10:30:00Z"
  },
  "observer": {
    "id": 1,
    "name": "my-observer",
    "displayName": "My Observer Service",
    "observerBaseUrl": "https://observer.example.com"
  },
  "receivedAt": "2024-01-15T10:30:00Z",
  "processedAt": null
}
```

**Response**: Return HTTP 200 OK (or any 2xx status) for successful processing. Return 4xx/5xx for errors.

#### Implementation Notes

- Notifications are sent synchronously, so your endpoint should return as soon as possible and process them in a
  separate thread
- The `event.eventContent` (V2) or `event.beforeState` and `event.afterState`(V1) contains the event-specific payload - see
  the specific service repository's README.md for event content schemas
- If your endpoint returns any type of error (4xx/5xx) or it is unreachable/times out, the notification status will be
  marked as `FAILED_TO_DELIVER` and can be
  replayed later
- After a Notification has been processed, the Observer should update the notification by calling the endpoint
  `PUT /api/v2/pp/notification/notifications/{notificationSequenceId}` overriding the Notification object with the
  status set at `PROCESSED` if everything went ok, `FAILED_TO_PROCESS` if an error occurred during the notification
  processing. In the last case it is also possible to specify the case of the error in the `errorMessage` field of the
  Notification.

### 3. Emit an Event

Emit a new event to the notification system. When an event is emitted:

1. The event is stored in the database
2. The system finds all observers subscribed to the event type (or subscribed to all events)
3. For each subscribed observer, a notification is created with status `PROCESSING`
4. Notifications are dispatched asynchronously to observer endpoints via HTTP POST
5. If dispatch fails, the notification status is updated to `FAILED_TO_DELIVER`

**Endpoint**: `POST /api/v2/pp/notification/events/emit`

**Request Body**:

```json
{
  "event": {
    "resourceType": "DATA_PRODUCT",
    "resourceIdentifier": "d5b5b9ac-6a73-4c73-b9ce-4bfc10a1dba0",
    "type": "DATA_PRODUCT_CREATED",
    "eventTypeVersion": "1.0.0",
    "eventContent": {
      "dataProductId": "d5b5b9ac-6a73-4c73-b9ce-4bfc10a1dba0",
      "name": "My Data Product",
      "version": "1.0.0"
    }
  }
}
```

**Response** (201 Created):

```json
{
  "event": {
    "sequenceId": 101,
    "resourceType": "DATA_PRODUCT",
    "resourceIdentifier": "d5b5b9ac-6a73-4c73-b9ce-4bfc10a1dba0",
    "type": "DATA_PRODUCT_CREATED",
    "eventTypeVersion": "1.0.0",
    "eventContent": {
      ...
      //See the specific service repository's README.md for the events signatures
    },
    "createdAt": "2024-01-15T10:30:00Z"
  }
}
```

**What Happens**:

- The event is persisted and assigned a unique `sequenceId`
- All observers subscribed to `DATA_PRODUCT_CREATED` (or subscribed to all events) receive a notification
- Each notification contains the full event details
- Notifications are sent asynchronously, so the API returns immediately after creating the event
- The notification delivery status is tracked separately and can be queried via the notifications API

### 4. Replay a Notification

Replay a notification that previously failed to deliver or process. This operation:

1. Resets the notification status to `PROCESSING`
2. Clears any previous error messages
3. Dispatches the notification again to the observer
4. Updates the status based on the dispatch result

**Endpoint**: `POST /api/v2/pp/notification/notifications/replay`

**Request Body**:

```json
{
  "notificationSequenceId": 101
}
```

**Response** (200 OK):

```json
{
  "notification": {
    "sequenceId": 101,
    "status": "PROCESSING",
    "subscription": {
      "uuid": "550e8400-e29b-41d4-a716-446655440000",
      "name": "my-observer",
      "displayName": "My Observer Service"
    },
    "event": {
      "sequenceId": 50,
      "type": "DATA_PRODUCT_CREATED",
      "resourceType": "DATA_PRODUCT",
      "resourceIdentifier": "d5b5b9ac-6a73-4c73-b9ce-4bfc10a1dba0"
    }
  }
}
```

**Notes**:

- The notification is dispatched only to the observer associated with the notification
- If the replay dispatch fails, the notification status will be updated to `FAILED_TO_DELIVER` with the error message
- The notification's error message is cleared during replay and will be populated again only if the dispatch fails

## Additional Endpoints

The service also provides endpoints for:

- **Querying Events**: `GET /api/v2/pp/notification/events` (with filtering)
- **Querying Notifications**: `GET /api/v2/pp/notification/notifications` (with filtering by subscription, event type,
  status)
- **Querying Subscriptions**: `GET /api/v2/pp/notification/subscriptions` (with filtering)
- **Managing Subscriptions**: Update, delete, and search subscriptions

See the Swagger UI for complete API documentation.

## Architecture

- **Spring Boot 3.5.6**: Core framework
- **PostgreSQL**: Primary database (with Flyway for migrations)
- **H2**: In-memory database for development
- **Spring Data JPA**: Data persistence
- **SpringDoc OpenAPI**: API documentation
- **Apache HttpClient**: HTTP client for observer communication

## License

Licensed under the Apache License, Version 2.0.
