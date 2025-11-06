package org.opendatamesh.platform.pp.notification.rest.v1.controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.opendatamesh.platform.pp.notification.rest.NotificationApplicationIT;
import org.opendatamesh.platform.pp.notification.rest.RoutesV1;
import org.opendatamesh.platform.pp.notification.rest.RoutesV2;
import org.opendatamesh.platform.pp.notification.rest.v1.resources.EventNotificationResV1;
import org.opendatamesh.platform.pp.notification.rest.v1.resources.EventNotificationStatusResV1;
import org.opendatamesh.platform.pp.notification.rest.v1.resources.EventResV1;
import org.opendatamesh.platform.pp.notification.rest.v1.resources.ObserverResV1;
import org.opendatamesh.platform.pp.notification.rest.v2.resources.subscription.SubscriptionRes;
import org.opendatamesh.platform.pp.notification.utils.client.jackson.PageUtility;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

public class EventNotificationControllerV1IT extends NotificationApplicationIT {

    @Test
    public void whenGetNotificationByIdThenReturnNotification() {
        // Given - Create observer using V1 API
        String observerName = "test-observer-" + System.currentTimeMillis();
        ObserverResV1 observer = new ObserverResV1();
        observer.setName(observerName);
        observer.setDisplayName("Test Observer");
        observer.setObserverServerBaseUrl("https://observer.example.com/api/v1");

        ResponseEntity<ObserverResV1> observerResponse = rest.postForEntity(
                apiUrl(RoutesV1.OBSERVERS),
                new HttpEntity<>(observer),
                ObserverResV1.class
        );
        assertThat(observerResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(observerResponse.getBody()).isNotNull();

        // Dispatch event using V1 API (this creates the event and notification)
        String entityId = "d5b5b9ac-6a73-4c73-b9ce-4bfc10a1dba0-" + System.currentTimeMillis();
        String eventType = "DATA_PRODUCT_CREATED";
        EventResV1 event = new EventResV1();
        event.setType(eventType);
        event.setEntityId(entityId);
        event.setBeforeState(jsonNode("{\"test\": \"before\"}"));
        event.setAfterState(jsonNode("{\"test\": \"after\"}"));

        ResponseEntity<Void> dispatchResponse = rest.postForEntity(
                apiUrl(RoutesV1.DISPATCH),
                new HttpEntity<>(event),
                Void.class
        );
        assertThat(dispatchResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        // Search for the created notification to get its ID
        ResponseEntity<PageUtility<EventNotificationResV1>> searchResponse = rest.exchange(
                apiUrl(RoutesV1.NOTIFICATIONS) + "?eventType=" + eventType + "&entityId=" + entityId,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );
        assertThat(searchResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(searchResponse.getBody()).isNotNull();
        Page<EventNotificationResV1> notificationsPage = searchResponse.getBody();
        assertThat(notificationsPage).isNotNull();
        assertThat(notificationsPage.getContent()).isNotEmpty();

        EventNotificationResV1 createdNotification = notificationsPage.getContent().stream()
                .filter(n -> n.getEvent() != null &&
                        eventType.equals(n.getEvent().getType()) &&
                        entityId.equals(n.getEvent().getEntityId()))
                .findFirst()
                .orElse(null);
        assertThat(createdNotification).isNotNull();
        Long notificationId = createdNotification.getId();

        // When
        ResponseEntity<EventNotificationResV1> response = rest.getForEntity(
                apiUrl(RoutesV1.NOTIFICATIONS, "/" + notificationId),
                EventNotificationResV1.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();

        assertThat(response.getBody().getId()).isEqualTo(notificationId);

        // Cleanup - use V2 API only for deletion
        rest.delete(apiUrl(RoutesV2.NOTIFICATIONS, "/" + notificationId));
        // Find and delete event
        ResponseEntity<PageUtility<EventResV1>> eventSearchResponse = rest.exchange(
                apiUrl(RoutesV1.EVENTS) + "?eventType=" + eventType + "&entityId=" + entityId,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );

        Long eventId = eventSearchResponse.getBody().getContent().get(0).getId();
        rest.delete(apiUrl(RoutesV2.EVENTS, "/" + eventId));

        // Find and delete subscription
        ResponseEntity<PageUtility<SubscriptionRes>> subscriptionSearchResponse = rest.exchange(
                apiUrl(RoutesV2.SUBSCRIPTIONS) + "?name=" + observerName,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );

        String subscriptionUuid = subscriptionSearchResponse.getBody().getContent().get(0).getUuid();
        rest.delete(apiUrl(RoutesV2.SUBSCRIPTIONS, "/" + subscriptionUuid));

    }

    @Test
    public void whenGetNotificationWithNonExistentIdThenReturnNotFound() {
        // Given
        Long nonExistentId = 999999L;

        // When
        ResponseEntity<String> response = rest.getForEntity(
                apiUrl(RoutesV1.NOTIFICATIONS, "/" + nonExistentId),
                String.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    public void whenUpdateNotificationThenReturnUpdatedNotification() {
        // Given - Create observer using V1 API
        String observerName = "test-observer-" + System.currentTimeMillis();
        ObserverResV1 observer = new ObserverResV1();
        observer.setName(observerName);
        observer.setDisplayName("Test Observer");
        observer.setObserverServerBaseUrl("https://observer.example.com/api/v1");

        ResponseEntity<ObserverResV1> observerResponse = rest.postForEntity(
                apiUrl(RoutesV1.OBSERVERS),
                new HttpEntity<>(observer),
                ObserverResV1.class
        );
        assertThat(observerResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(observerResponse.getBody()).isNotNull();

        // Dispatch event using V1 API (this creates the event and notification)
        String entityId = "d5b5b9ac-6a73-4c73-b9ce-4bfc10a1dba0-" + System.currentTimeMillis();
        String eventType = "DATA_PRODUCT_CREATED";
        EventResV1 event = new EventResV1();
        event.setType(eventType);
        event.setEntityId(entityId);
        event.setBeforeState(jsonNode("{\"test\": \"before\"}"));
        event.setAfterState(jsonNode("{\"test\": \"after\"}"));

        ResponseEntity<Void> dispatchResponse = rest.postForEntity(
                apiUrl(RoutesV1.DISPATCH),
                new HttpEntity<>(event),
                Void.class
        );
        assertThat(dispatchResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        // Search for the created notification to get its ID
        ResponseEntity<PageUtility<EventNotificationResV1>> searchResponse = rest.exchange(
                apiUrl(RoutesV1.NOTIFICATIONS) + "?eventType=" + eventType + "&entityId=" + entityId,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );
        assertThat(searchResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(searchResponse.getBody()).isNotNull();
        Page<EventNotificationResV1> notificationsPage = searchResponse.getBody();
        assertThat(notificationsPage).isNotNull();
        assertThat(notificationsPage.getContent()).isNotEmpty();

        EventNotificationResV1 createdNotification = notificationsPage.getContent().stream()
                .filter(n -> n.getEvent() != null &&
                        eventType.equals(n.getEvent().getType()) &&
                        entityId.equals(n.getEvent().getEntityId()))
                .findFirst()
                .orElse(null);
        assertThat(createdNotification).isNotNull();
        Long notificationId = createdNotification.getId();

        EventNotificationResV1 updateRequest = new EventNotificationResV1();
        updateRequest.setStatus(EventNotificationStatusResV1.PROCESSED);
        updateRequest.setProcessingOutput("Successfully processed");

        // When
        ResponseEntity<EventNotificationResV1> response = rest.exchange(
                apiUrl(RoutesV1.NOTIFICATIONS, "/" + notificationId),
                HttpMethod.PUT,
                new HttpEntity<>(updateRequest),
                EventNotificationResV1.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getId()).isEqualTo(notificationId);
        assertThat(response.getBody().getStatus()).isEqualTo(EventNotificationStatusResV1.PROCESSED);
        assertThat(response.getBody().getProcessingOutput()).isEqualTo("Successfully processed");


        // Cleanup - use V2 API only for deletion
        rest.delete(apiUrl(RoutesV2.NOTIFICATIONS, "/" + notificationId));
        // Find and delete event
        ResponseEntity<PageUtility<EventResV1>> eventSearchResponse = rest.exchange(
                apiUrl(RoutesV1.EVENTS) + "?eventType=" + eventType + "&entityId=" + entityId,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );

        Long eventId = eventSearchResponse.getBody().getContent().get(0).getId();
        rest.delete(apiUrl(RoutesV2.EVENTS, "/" + eventId));

        // Find and delete subscription
        ResponseEntity<PageUtility<SubscriptionRes>> subscriptionSearchResponse = rest.exchange(
                apiUrl(RoutesV2.SUBSCRIPTIONS) + "?name=" + observerName,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );

        String subscriptionUuid = subscriptionSearchResponse.getBody().getContent().get(0).getUuid();
        rest.delete(apiUrl(RoutesV2.SUBSCRIPTIONS, "/" + subscriptionUuid));

    }

    @Test
    public void whenUpdateNotificationWithNonExistentIdThenReturnNotFound() {
        // Given
        Long nonExistentId = 999999L;
        EventNotificationResV1 notification = new EventNotificationResV1();
        notification.setStatus(EventNotificationStatusResV1.PROCESSED);

        // When
        ResponseEntity<String> response = rest.exchange(
                apiUrl(RoutesV1.NOTIFICATIONS, "/" + nonExistentId),
                HttpMethod.PUT,
                new HttpEntity<>(notification),
                String.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    public void whenSearchNotificationsThenReturnNotificationsList() {
        // Given - Create observer and dispatch event using V1 API
        String observerName = "test-observer-" + System.currentTimeMillis();
        ObserverResV1 observer = new ObserverResV1();
        observer.setName(observerName);
        observer.setDisplayName("Test Observer");
        observer.setObserverServerBaseUrl("https://observer.example.com/api/v1");

        ResponseEntity<ObserverResV1> observerResponse = rest.postForEntity(
                apiUrl(RoutesV1.OBSERVERS),
                new HttpEntity<>(observer),
                ObserverResV1.class
        );
        assertThat(observerResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(observerResponse.getBody()).isNotNull();

        // Dispatch event using V1 API (this creates the event and notification)
        String entityId = "d5b5b9ac-6a73-4c73-b9ce-4bfc10a1dba0-" + System.currentTimeMillis();
        String eventType = "DATA_PRODUCT_CREATED";
        EventResV1 event = new EventResV1();
        event.setType(eventType);
        event.setEntityId(entityId);
        event.setBeforeState(jsonNode("{\"test\": \"before\"}"));
        event.setAfterState(jsonNode("{\"test\": \"after\"}"));

        ResponseEntity<Void> dispatchResponse = rest.postForEntity(
                apiUrl(RoutesV1.DISPATCH),
                new HttpEntity<>(event),
                Void.class
        );
        assertThat(dispatchResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        // When - Search for all notifications (no filters)
        ResponseEntity<PageUtility<EventNotificationResV1>> response = rest.exchange(
                apiUrl(RoutesV1.NOTIFICATIONS),
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();

        Page<EventNotificationResV1> notificationsPage = response.getBody();
        assertThat(notificationsPage).isNotNull();
        assertThat(notificationsPage.getContent()).isNotEmpty();

        // Verify our notification is in the results
        boolean foundOurNotification = notificationsPage.getContent().stream()
                .anyMatch(n -> n.getEvent() != null &&
                        eventType.equals(n.getEvent().getType()) &&
                        entityId.equals(n.getEvent().getEntityId()));
        assertThat(foundOurNotification).isTrue();

        // Cleanup - use V2 API only for deletion
        // Find and delete notification
        ResponseEntity<PageUtility<EventNotificationResV1>> searchResponse = rest.exchange(
                apiUrl(RoutesV1.NOTIFICATIONS) + "?eventType=" + eventType + "&entityId=" + entityId,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );

        Long notificationId = searchResponse.getBody().getContent().get(0).getId();
        rest.delete(apiUrl(RoutesV2.NOTIFICATIONS, "/" + notificationId));

        // Find and delete event
        ResponseEntity<PageUtility<EventResV1>> eventSearchResponse = rest.exchange(
                apiUrl(RoutesV1.EVENTS) + "?eventType=" + eventType + "&entityId=" + entityId,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );

        Long eventId = eventSearchResponse.getBody().getContent().get(0).getId();
        rest.delete(apiUrl(RoutesV2.EVENTS, "/" + eventId));

        // Find and delete subscription
        ResponseEntity<PageUtility<SubscriptionRes>> subscriptionSearchResponse = rest.exchange(
                apiUrl(RoutesV2.SUBSCRIPTIONS) + "?name=" + observerName,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );

        String subscriptionUuid = subscriptionSearchResponse.getBody().getContent().get(0).getUuid();
        rest.delete(apiUrl(RoutesV2.SUBSCRIPTIONS, "/" + subscriptionUuid));

    }

    @Test
    public void whenSearchNotificationsWithEventTypeThenReturnFilteredNotifications() {
        // Given - Create two observers and dispatch two events using V1 API
        long timestamp = System.currentTimeMillis();

        // Create first observer
        String observerName1 = "test-observer-1-" + timestamp;
        ObserverResV1 observer1 = new ObserverResV1();
        observer1.setName(observerName1);
        observer1.setDisplayName("Test Observer 1");
        observer1.setObserverServerBaseUrl("https://observer1.example.com/api/v1");

        ResponseEntity<ObserverResV1> observerResponse1 = rest.postForEntity(
                apiUrl(RoutesV1.OBSERVERS),
                new HttpEntity<>(observer1),
                ObserverResV1.class
        );
        assertThat(observerResponse1.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(observerResponse1.getBody()).isNotNull();

        // Create second observer
        String observerName2 = "test-observer-2-" + timestamp;
        ObserverResV1 observer2 = new ObserverResV1();
        observer2.setName(observerName2);
        observer2.setDisplayName("Test Observer 2");
        observer2.setObserverServerBaseUrl("https://observer2.example.com/api/v1");

        ResponseEntity<ObserverResV1> observerResponse2 = rest.postForEntity(
                apiUrl(RoutesV1.OBSERVERS),
                new HttpEntity<>(observer2),
                ObserverResV1.class
        );
        assertThat(observerResponse2.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(observerResponse2.getBody()).isNotNull();

        // Dispatch first event using V1 API (this creates the event and notifications for both observers)
        String entityId1 = "d5b5b9ac-6a73-4c73-b9ce-4bfc10a1dba0-" + timestamp;
        String eventType = "DATA_PRODUCT_CREATED";
        EventResV1 event1 = new EventResV1();
        event1.setType(eventType);
        event1.setEntityId(entityId1);
        event1.setBeforeState(jsonNode("{\"test\": \"before1\"}"));
        event1.setAfterState(jsonNode("{\"test\": \"after1\"}"));

        ResponseEntity<Void> dispatchResponse1 = rest.postForEntity(
                apiUrl(RoutesV1.DISPATCH),
                new HttpEntity<>(event1),
                Void.class
        );
        assertThat(dispatchResponse1.getStatusCode()).isEqualTo(HttpStatus.OK);

        // Dispatch second event using V1 API
        String entityId2 = "d5b5b9ac-6a73-4c73-b9ce-4bfc10a1dba1-" + timestamp;
        EventResV1 event2 = new EventResV1();
        event2.setType(eventType);
        event2.setEntityId(entityId2);
        event2.setBeforeState(jsonNode("{\"test\": \"before2\"}"));
        event2.setAfterState(jsonNode("{\"test\": \"after2\"}"));

        ResponseEntity<Void> dispatchResponse2 = rest.postForEntity(
                apiUrl(RoutesV1.DISPATCH),
                new HttpEntity<>(event2),
                Void.class
        );
        assertThat(dispatchResponse2.getStatusCode()).isEqualTo(HttpStatus.OK);

        // Dispatch a third event with a different event type to verify filtering works correctly
        String entityId3 = "d5b5b9ac-6a73-4c73-b9ce-4bfc10a1dba2-" + timestamp;
        String differentEventType = "DATA_PRODUCT_UPDATED";
        EventResV1 event3 = new EventResV1();
        event3.setType(differentEventType);
        event3.setEntityId(entityId3);
        event3.setBeforeState(jsonNode("{\"test\": \"before3\"}"));
        event3.setAfterState(jsonNode("{\"test\": \"after3\"}"));

        ResponseEntity<Void> dispatchResponse3 = rest.postForEntity(
                apiUrl(RoutesV1.DISPATCH),
                new HttpEntity<>(event3),
                Void.class
        );
        assertThat(dispatchResponse3.getStatusCode()).isEqualTo(HttpStatus.OK);

        // When - Search for notifications by event type
        ResponseEntity<PageUtility<EventNotificationResV1>> response = rest.exchange(
                apiUrl(RoutesV1.NOTIFICATIONS) + "?eventType=" + eventType,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();

        Page<EventNotificationResV1> notificationsPage = response.getBody();
        assertThat(notificationsPage).isNotNull();
        assertThat(notificationsPage.getContent().size()).isGreaterThanOrEqualTo(4); // 2 observers × 2 events = 4 notifications

        // Verify ALL notifications in the filtered results have the correct event type
        boolean allHaveCorrectEventType = notificationsPage.getContent().stream()
                .allMatch(n -> n.getEvent() != null && eventType.equals(n.getEvent().getType()));
        assertThat(allHaveCorrectEventType).isTrue();

        // Verify NO notifications with different event type are in the results
        boolean noneHaveDifferentEventType = notificationsPage.getContent().stream()
                .noneMatch(n -> n.getEvent() != null && differentEventType.equals(n.getEvent().getType()));
        assertThat(noneHaveDifferentEventType).isTrue();

        // Verify our notifications are in the results
        long countEvent1Notifications = notificationsPage.getContent().stream()
                .filter(n -> n.getEvent() != null &&
                        eventType.equals(n.getEvent().getType()) &&
                        entityId1.equals(n.getEvent().getEntityId()))
                .count();
        assertThat(countEvent1Notifications).isGreaterThanOrEqualTo(2); // At least 2 notifications for event1 (one per observer)

        long countEvent2Notifications = notificationsPage.getContent().stream()
                .filter(n -> n.getEvent() != null &&
                        eventType.equals(n.getEvent().getType()) &&
                        entityId2.equals(n.getEvent().getEntityId()))
                .count();
        assertThat(countEvent2Notifications).isGreaterThanOrEqualTo(2); // At least 2 notifications for event2 (one per observer)

        // Cleanup - use V2 API only for deletion
        // Find and delete notifications for event1
        ResponseEntity<PageUtility<EventNotificationResV1>> searchResponse1 = rest.exchange(
                apiUrl(RoutesV1.NOTIFICATIONS) + "?eventType=" + eventType + "&entityId=" + entityId1,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );
        searchResponse1.getBody().getContent().forEach(n -> rest.delete(apiUrl(RoutesV2.NOTIFICATIONS, "/" + n.getId())));

        // Find and delete notifications for event2
        ResponseEntity<PageUtility<EventNotificationResV1>> searchResponse2 = rest.exchange(
                apiUrl(RoutesV1.NOTIFICATIONS) + "?eventType=" + eventType + "&entityId=" + entityId2,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );
        searchResponse2.getBody().getContent().forEach(n -> rest.delete(apiUrl(RoutesV2.NOTIFICATIONS, "/" + n.getId())));

        // Find and delete notifications for event3 (different event type)
        ResponseEntity<PageUtility<EventNotificationResV1>> searchResponse3 = rest.exchange(
                apiUrl(RoutesV1.NOTIFICATIONS) + "?eventType=" + differentEventType + "&entityId=" + entityId3,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );
        searchResponse3.getBody().getContent().forEach(n -> rest.delete(apiUrl(RoutesV2.NOTIFICATIONS, "/" + n.getId())));

        // Find and delete events
        ResponseEntity<PageUtility<EventResV1>> eventSearchResponse1 = rest.exchange(
                apiUrl(RoutesV1.EVENTS) + "?eventType=" + eventType + "&entityId=" + entityId1,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );
        Long eventId1 = eventSearchResponse1.getBody().getContent().get(0).getId();
        rest.delete(apiUrl(RoutesV2.EVENTS, "/" + eventId1));

        ResponseEntity<PageUtility<EventResV1>> eventSearchResponse2 = rest.exchange(
                apiUrl(RoutesV1.EVENTS) + "?eventType=" + eventType + "&entityId=" + entityId2,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );
        Long eventId2 = eventSearchResponse2.getBody().getContent().get(0).getId();
        rest.delete(apiUrl(RoutesV2.EVENTS, "/" + eventId2));

        // Find and delete subscriptions
        ResponseEntity<PageUtility<SubscriptionRes>> subscriptionSearchResponse1 = rest.exchange(
                apiUrl(RoutesV2.SUBSCRIPTIONS) + "?name=" + observerName1,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );
        String subscriptionUuid1 = subscriptionSearchResponse1.getBody().getContent().get(0).getUuid();
        rest.delete(apiUrl(RoutesV2.SUBSCRIPTIONS, "/" + subscriptionUuid1));

        ResponseEntity<PageUtility<SubscriptionRes>> subscriptionSearchResponse2 = rest.exchange(
                apiUrl(RoutesV2.SUBSCRIPTIONS) + "?name=" + observerName2,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );
        String subscriptionUuid2 = subscriptionSearchResponse2.getBody().getContent().get(0).getUuid();
        rest.delete(apiUrl(RoutesV2.SUBSCRIPTIONS, "/" + subscriptionUuid2));

    }

    @Test
    public void whenSearchNotificationsWithStatusThenReturnFilteredNotifications() {
        // Given - Create two observers and dispatch two events using V1 API
        long timestamp = System.currentTimeMillis();

        // Create first observer
        String observerName1 = "test-observer-1-" + timestamp;
        ObserverResV1 observer1 = new ObserverResV1();
        observer1.setName(observerName1);
        observer1.setDisplayName("Test Observer 1");
        observer1.setObserverServerBaseUrl("https://observer1.example.com/api/v1");

        ResponseEntity<ObserverResV1> observerResponse1 = rest.postForEntity(
                apiUrl(RoutesV1.OBSERVERS),
                new HttpEntity<>(observer1),
                ObserverResV1.class
        );
        assertThat(observerResponse1.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(observerResponse1.getBody()).isNotNull();

        // Create second observer
        String observerName2 = "test-observer-2-" + timestamp;
        ObserverResV1 observer2 = new ObserverResV1();
        observer2.setName(observerName2);
        observer2.setDisplayName("Test Observer 2");
        observer2.setObserverServerBaseUrl("https://observer2.example.com/api/v1");

        ResponseEntity<ObserverResV1> observerResponse2 = rest.postForEntity(
                apiUrl(RoutesV1.OBSERVERS),
                new HttpEntity<>(observer2),
                ObserverResV1.class
        );
        assertThat(observerResponse2.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(observerResponse2.getBody()).isNotNull();

        // Dispatch first event using V1 API (this creates the event and notifications for both observers)
        String entityId1 = "d5b5b9ac-6a73-4c73-b9ce-4bfc10a1dba0-" + timestamp;
        String eventType = "DATA_PRODUCT_CREATED";
        EventResV1 event1 = new EventResV1();
        event1.setType(eventType);
        event1.setEntityId(entityId1);
        event1.setBeforeState(jsonNode("{\"test\": \"before1\"}"));
        event1.setAfterState(jsonNode("{\"test\": \"after1\"}"));

        ResponseEntity<Void> dispatchResponse1 = rest.postForEntity(
                apiUrl(RoutesV1.DISPATCH),
                new HttpEntity<>(event1),
                Void.class
        );
        assertThat(dispatchResponse1.getStatusCode()).isEqualTo(HttpStatus.OK);

        // Dispatch second event using V1 API
        String entityId2 = "d5b5b9ac-6a73-4c73-b9ce-4bfc10a1dba1-" + timestamp;
        EventResV1 event2 = new EventResV1();
        event2.setType(eventType);
        event2.setEntityId(entityId2);
        event2.setBeforeState(jsonNode("{\"test\": \"before2\"}"));
        event2.setAfterState(jsonNode("{\"test\": \"after2\"}"));

        ResponseEntity<Void> dispatchResponse2 = rest.postForEntity(
                apiUrl(RoutesV1.DISPATCH),
                new HttpEntity<>(event2),
                Void.class
        );
        assertThat(dispatchResponse2.getStatusCode()).isEqualTo(HttpStatus.OK);

        // Find notifications for first event and update one to PROCESSED status
        ResponseEntity<PageUtility<EventNotificationResV1>> searchResponse1 = rest.exchange(
                apiUrl(RoutesV1.NOTIFICATIONS) + "?eventType=" + eventType + "&entityId=" + entityId1,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );
        assertThat(searchResponse1.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(searchResponse1.getBody()).isNotNull();
        Page<EventNotificationResV1> notificationsPage1 = searchResponse1.getBody();
        assertThat(notificationsPage1).isNotNull();
        assertThat(notificationsPage1.getContent()).isNotEmpty();

        // Update first notification to PROCESSED status
        EventNotificationResV1 notificationToUpdate = notificationsPage1.getContent().get(0);
        Long notificationId = notificationToUpdate.getId();

        EventNotificationResV1 updateRequest = new EventNotificationResV1();
        updateRequest.setStatus(EventNotificationStatusResV1.PROCESSED);
        rest.exchange(
                apiUrl(RoutesV1.NOTIFICATIONS, "/" + notificationId),
                HttpMethod.PUT,
                new HttpEntity<>(updateRequest),
                EventNotificationResV1.class
        );

        EventNotificationStatusResV1 status = EventNotificationStatusResV1.PROCESSED;

        // When - Search for notifications by PROCESSED status
        ResponseEntity<PageUtility<EventNotificationResV1>> response = rest.exchange(
                apiUrl(RoutesV1.NOTIFICATIONS) + "?notificationStatus=" + status,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();

        // Verify our updated notification is in the filtered results
        Page<EventNotificationResV1> filteredNotifications = response.getBody();
        assertThat(filteredNotifications).isNotNull();
        boolean foundOurNotification = filteredNotifications.getContent().stream()
                .anyMatch(n -> notificationId.equals(n.getId()));
        assertThat(foundOurNotification).isTrue();

        // Verify the notification has PROCESSED status
        EventNotificationResV1 foundNotification = filteredNotifications.getContent().stream()
                .filter(n -> notificationId.equals(n.getId()))
                .findFirst()
                .orElse(null);
        assertThat(foundNotification).isNotNull();
        assertThat(foundNotification.getStatus()).isEqualTo(EventNotificationStatusResV1.PROCESSED);

        // Cleanup - use V2 API only for deletion
        // Find and delete all notifications for event1
        ResponseEntity<PageUtility<EventNotificationResV1>> cleanupSearchResponse1 = rest.exchange(
                apiUrl(RoutesV1.NOTIFICATIONS) + "?eventType=" + eventType + "&entityId=" + entityId1,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );
        cleanupSearchResponse1.getBody().getContent().forEach(n -> rest.delete(apiUrl(RoutesV2.NOTIFICATIONS, "/" + n.getId())));

        // Find and delete all notifications for event2
        ResponseEntity<PageUtility<EventNotificationResV1>> cleanupSearchResponse2 = rest.exchange(
                apiUrl(RoutesV1.NOTIFICATIONS) + "?eventType=" + eventType + "&entityId=" + entityId2,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );
        cleanupSearchResponse2.getBody().getContent().forEach(n -> rest.delete(apiUrl(RoutesV2.NOTIFICATIONS, "/" + n.getId())));

        // Find and delete events
        ResponseEntity<PageUtility<EventResV1>> eventSearchResponse1 = rest.exchange(
                apiUrl(RoutesV1.EVENTS) + "?eventType=" + eventType + "&entityId=" + entityId1,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );
        Long eventId1 = eventSearchResponse1.getBody().getContent().get(0).getId();
        rest.delete(apiUrl(RoutesV2.EVENTS, "/" + eventId1));

        ResponseEntity<PageUtility<EventResV1>> eventSearchResponse2 = rest.exchange(
                apiUrl(RoutesV1.EVENTS) + "?eventType=" + eventType + "&entityId=" + entityId2,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );
        Long eventId2 = eventSearchResponse2.getBody().getContent().get(0).getId();
        rest.delete(apiUrl(RoutesV2.EVENTS, "/" + eventId2));

        // Find and delete subscriptions
        ResponseEntity<PageUtility<SubscriptionRes>> subscriptionSearchResponse1 = rest.exchange(
                apiUrl(RoutesV2.SUBSCRIPTIONS) + "?name=" + observerName1,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );
        String subscriptionUuid1 = subscriptionSearchResponse1.getBody().getContent().get(0).getUuid();
        rest.delete(apiUrl(RoutesV2.SUBSCRIPTIONS, "/" + subscriptionUuid1));

        ResponseEntity<PageUtility<SubscriptionRes>> subscriptionSearchResponse2 = rest.exchange(
                apiUrl(RoutesV2.SUBSCRIPTIONS) + "?name=" + observerName2,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );
        String subscriptionUuid2 = subscriptionSearchResponse2.getBody().getContent().get(0).getUuid();
        rest.delete(apiUrl(RoutesV2.SUBSCRIPTIONS, "/" + subscriptionUuid2));

    }


    private final ObjectMapper objectMapper = new ObjectMapper();

    private JsonNode jsonNode(String json) {
        try {
            return objectMapper.readTree(json);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

