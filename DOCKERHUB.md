# ODM Platform Notification Server

The Notification Server is the product-plane event hub of [Open Data Mesh Platform](https://github.com/opendatamesh-initiative) V2. ODM services emit events to it (for example, when a data product is created or a version is published), and it delivers a notification to every observer subscribed to that event type, over HTTP. It stores events and notifications, tracks the delivery and processing status of each notification, and can replay failed ones. Services such as the [Registry](https://hub.docker.com/r/opendatamesh/odm-platform-registry) depend on it for their event-driven lifecycle.

- **Source code:** [opendatamesh-initiative/odm-platform-pp-notification-server](https://github.com/opendatamesh-initiative/odm-platform-pp-notification-server)
- **Documentation:** [README](https://github.com/opendatamesh-initiative/odm-platform-pp-notification-server/blob/main/README.md)
- **Issues:** [GitHub issues](https://github.com/opendatamesh-initiative/odm-platform-pp-notification-server/issues)

## Tags

Each [GitHub release](https://github.com/opendatamesh-initiative/odm-platform-pp-notification-server/releases) publishes one image tagged with its version, without the `v` prefix:

```bash
docker pull opendatamesh/odm-platform-notification:<version>
```

There is no `latest` tag: always pin a version. Tags before `2.0.0` are ODM Platform V1 images, built from [odm-platform](https://github.com/opendatamesh-initiative/odm-platform), and are configured differently. V1 maintenance releases (`1.0.x`) are still published to this repository, so the most recently pushed tag isn't necessarily a V2 image.

## Requirements

| Dependency | Required | Notes |
| --- | --- | --- |
| PostgreSQL | Yes | Flyway creates and migrates the schema on startup. |
| Observers | No | Services that subscribe to events must expose `POST /api/v1/up/observer/notifications` or `POST /api/v2/up/observer/notifications` on a URL the Notification Server can reach. |

## Quick start

Starts the Notification Server against an existing PostgreSQL database:

```bash
docker run --name odm-notification -p 8080:8080 \
  -e DB_JDBC_URL=jdbc:postgresql://db.example.com:5432/notification \
  -e DB_USERNAME=notification \
  -e DB_PASSWORD=change-me \
  opendatamesh/odm-platform-notification:<version>
```

When it's up, the API (prefix `/api/v2/pp/notification/`, plus the V1 compatibility API under `/api/v1/pp/notification/`) is documented at `http://localhost:8080/swagger-ui.html` (OpenAPI at `/v3/api-docs`). Health is at `/actuator/health`.

Other ODM services reach it through their Notification address setting, for example `http://notification.example.com:8080`. Some of their examples use port `8006`: map the port or set `server.port` to match.

## How delivery works

1. An observer subscribes with `POST /api/v2/pp/notification/subscriptions/subscribe`, giving its `observerBaseUrl`, its `observerApiVersion` (`v1` or `v2`) and the event types it wants. With no event types, it receives all events.
2. A service emits an event with `POST /api/v2/pp/notification/events/emit`. The event is stored and the call returns.
3. For each subscribed observer, the server creates a notification and sends it in the background to `{observerBaseUrl}/api/{observerApiVersion}/up/observer/notifications`. Events with a `2.x` `eventTypeVersion` aren't sent to `v1` observers.
4. If the observer returns an error or can't be reached within 60 seconds, the notification is marked `FAILED_TO_DELIVER`. It can be resent with `POST /api/v2/pp/notification/notifications/replay`.
5. The observer reports the outcome with `PUT /api/v2/pp/notification/notifications/{sequenceId}`, setting the status to `PROCESSED` or `FAILED_TO_PROCESS`.

Payloads and examples: [README](https://github.com/opendatamesh-initiative/odm-platform-pp-notification-server/blob/main/README.md#main-use-cases).

## Environment variables

### Image

| Variable | Description | Default |
| --- | --- | --- |
| `DB_JDBC_URL` | JDBC URL of the PostgreSQL database (`spring.datasource.url`). | none, required |
| `DB_USERNAME` | Database user (`spring.datasource.username`). | none, required |
| `DB_PASSWORD` | Database password (`spring.datasource.password`). | none, required |
| `PROFILES_ACTIVE` | Spring profiles to activate. The `docker` profile maps the three `DB_*` variables above. | `docker` |
| `JAVA_OPTS` | Extra JVM options, for example `-Xmx512m`. | empty |
| `SPRING_PROPS` | Extra arguments added to the `java` command line before `-jar`, such as `-Dserver.port=9000`. It does not accept JSON: use `SPRING_APPLICATION_JSON` for that. | empty |

### Application

Every property can be set with `SPRING_APPLICATION_JSON` or as an environment variable, using Spring Boot relaxed binding (for example, `server.port` becomes `SERVER_PORT`).

| Property | Description | Default |
| --- | --- | --- |
| `server.port` | HTTP port. | `8080` |
| `spring.jpa.properties.hibernate.default_schema` | Database schema used by Hibernate and Flyway. Lowercase only, or the server doesn't start. | `odm_notification` |

Timeouts and the connection pool used to call observers are fixed in the code and can't be configured.

## Ports and volumes

- **Port:** `8080` (HTTP), or the value of `server.port`.
- **Volumes:** none. All state is in PostgreSQL.

## Learn more

- [Subscribe an observer](https://github.com/opendatamesh-initiative/odm-platform-pp-notification-server/blob/main/README.md#1-subscribe-a-new-observer)
- [Build an observer](https://github.com/opendatamesh-initiative/odm-platform-pp-notification-server/blob/main/README.md#2-building-a-new-observer)
- [Emit an event](https://github.com/opendatamesh-initiative/odm-platform-pp-notification-server/blob/main/README.md#3-emit-an-event) and [replay a notification](https://github.com/opendatamesh-initiative/odm-platform-pp-notification-server/blob/main/README.md#4-replay-a-notification)

Licensed under the [Apache License 2.0](https://www.apache.org/licenses/LICENSE-2.0).
