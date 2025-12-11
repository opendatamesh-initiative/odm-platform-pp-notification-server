package org.opendatamesh.platform.pp.notification.rest.v2.controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.opendatamesh.platform.pp.notification.rest.NotificationApplicationIT;
import org.opendatamesh.platform.pp.notification.rest.RoutesV2;
import org.opendatamesh.platform.pp.notification.rest.v2.resources.event.EventRes;
import org.opendatamesh.platform.pp.notification.rest.v2.resources.notification.NotificationRes;
import org.opendatamesh.platform.pp.notification.rest.v2.resources.notification.NotificationStatusRes;
import org.opendatamesh.platform.pp.notification.rest.v2.resources.subscription.SubscriptionEventTypeRes;
import org.opendatamesh.platform.pp.notification.rest.v2.resources.subscription.SubscriptionRes;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class NotificationControllerIT extends NotificationApplicationIT {

    private final ObjectMapper objectMapper = new ObjectMapper();

    private JsonNode jsonNode(String json) {
        try {
            return objectMapper.readTree(json);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void whenCreateNotificationThenReturnCreatedNotification() {
        // Given - Create event and subscription first
        EventRes event = createTestEvent();
        ResponseEntity<EventRes> eventResponse = rest.postForEntity(
                apiUrl(RoutesV2.EVENTS),
                new HttpEntity<>(event),
                EventRes.class
        );
        assertThat(eventResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        EventRes createdEvent = eventResponse.getBody();

        SubscriptionRes subscription = createTestSubscription();
        ResponseEntity<SubscriptionRes> subscriptionResponse = rest.postForEntity(
                apiUrl(RoutesV2.SUBSCRIPTIONS),
                new HttpEntity<>(subscription),
                SubscriptionRes.class
        );
        assertThat(subscriptionResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        SubscriptionRes createdSubscription = subscriptionResponse.getBody();

        // Create notification
        NotificationRes notification = createTestNotification(createdEvent, createdSubscription);

        // When
        ResponseEntity<NotificationRes> response = rest.postForEntity(
                apiUrl(RoutesV2.NOTIFICATIONS),
                new HttpEntity<>(notification),
                NotificationRes.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getSequenceId()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo(NotificationStatusRes.PROCESSING);

        // Cleanup
        if (response.getBody() != null && response.getBody().getSequenceId() != null) {
            rest.delete(apiUrl(RoutesV2.NOTIFICATIONS, "/" + response.getBody().getSequenceId()));
        }
        rest.delete(apiUrl(RoutesV2.EVENTS, "/" + createdEvent.getSequenceId()));
        rest.delete(apiUrl(RoutesV2.SUBSCRIPTIONS, "/" + createdSubscription.getUuid()));
    }

    @Test
    public void whenGetNotificationByIdThenReturnNotification() {
        // Given - Create event, subscription, and notification
        EventRes event = createTestEvent();
        ResponseEntity<EventRes> eventResponse = rest.postForEntity(
                apiUrl(RoutesV2.EVENTS),
                new HttpEntity<>(event),
                EventRes.class
        );
        assertThat(eventResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        EventRes createdEvent = eventResponse.getBody();

        SubscriptionRes subscription = createTestSubscription();
        ResponseEntity<SubscriptionRes> subscriptionResponse = rest.postForEntity(
                apiUrl(RoutesV2.SUBSCRIPTIONS),
                new HttpEntity<>(subscription),
                SubscriptionRes.class
        );
        assertThat(subscriptionResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        SubscriptionRes createdSubscription = subscriptionResponse.getBody();

        NotificationRes notification = createTestNotification(createdEvent, createdSubscription);

        ResponseEntity<NotificationRes> createResponse = rest.postForEntity(
                apiUrl(RoutesV2.NOTIFICATIONS),
                new HttpEntity<>(notification),
                NotificationRes.class
        );
        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        Long notificationId = createResponse.getBody().getSequenceId();

        // When
        ResponseEntity<NotificationRes> response = rest.getForEntity(
                apiUrl(RoutesV2.NOTIFICATIONS, "/" + notificationId),
                NotificationRes.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getSequenceId()).isEqualTo(notificationId);
        assertThat(response.getBody().getStatus()).isEqualTo(NotificationStatusRes.PROCESSING);

        // Cleanup
        rest.delete(apiUrl(RoutesV2.NOTIFICATIONS, "/" + notificationId));
        rest.delete(apiUrl(RoutesV2.EVENTS, "/" + createdEvent.getSequenceId()));
        rest.delete(apiUrl(RoutesV2.SUBSCRIPTIONS, "/" + createdSubscription.getUuid()));
    }

    @Test
    public void whenGetNotificationWithNonExistentIdThenReturnNotFound() {
        // Given
        Long nonExistentId = 999999L;

        // When
        ResponseEntity<String> response = rest.getForEntity(
                apiUrl(RoutesV2.NOTIFICATIONS, "/" + nonExistentId),
                String.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    public void whenSearchNotificationsThenReturnNotificationsList() {
        // Given - Create event and subscription
        EventRes event = createTestEvent();
        ResponseEntity<EventRes> eventResponse = rest.postForEntity(
                apiUrl(RoutesV2.EVENTS),
                new HttpEntity<>(event),
                EventRes.class
        );
        assertThat(eventResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        EventRes createdEvent = eventResponse.getBody();

        SubscriptionRes subscription = createTestSubscription();
        ResponseEntity<SubscriptionRes> subscriptionResponse = rest.postForEntity(
                apiUrl(RoutesV2.SUBSCRIPTIONS),
                new HttpEntity<>(subscription),
                SubscriptionRes.class
        );
        assertThat(subscriptionResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        SubscriptionRes createdSubscription = subscriptionResponse.getBody();

        // Create first notification
        NotificationRes notification1 = createTestNotification(createdEvent, createdSubscription);

        ResponseEntity<NotificationRes> createResponse1 = rest.postForEntity(
                apiUrl(RoutesV2.NOTIFICATIONS),
                new HttpEntity<>(notification1),
                NotificationRes.class
        );
        assertThat(createResponse1.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        Long notification1Id = createResponse1.getBody().getSequenceId();

        // Create second notification
        NotificationRes notification2 = createTestNotification(createdEvent, createdSubscription);

        ResponseEntity<NotificationRes> createResponse2 = rest.postForEntity(
                apiUrl(RoutesV2.NOTIFICATIONS),
                new HttpEntity<>(notification2),
                NotificationRes.class
        );
        assertThat(createResponse2.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        Long notification2Id = createResponse2.getBody().getSequenceId();

        // When
        ResponseEntity<String> response = rest.getForEntity(
                apiUrl(RoutesV2.NOTIFICATIONS),
                String.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();

        // Cleanup
        rest.delete(apiUrl(RoutesV2.NOTIFICATIONS, "/" + notification1Id));
        rest.delete(apiUrl(RoutesV2.NOTIFICATIONS, "/" + notification2Id));
        rest.delete(apiUrl(RoutesV2.EVENTS, "/" + createdEvent.getSequenceId()));
        rest.delete(apiUrl(RoutesV2.SUBSCRIPTIONS, "/" + createdSubscription.getUuid()));
    }

    @Test
    public void whenUpdateNotificationThenReturnUpdatedNotification() {
        // Given - Create event, subscription, and initial notification
        EventRes event = createTestEvent();
        ResponseEntity<EventRes> eventResponse = rest.postForEntity(
                apiUrl(RoutesV2.EVENTS),
                new HttpEntity<>(event),
                EventRes.class
        );
        assertThat(eventResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        EventRes createdEvent = eventResponse.getBody();

        SubscriptionRes subscription = createTestSubscription();
        ResponseEntity<SubscriptionRes> subscriptionResponse = rest.postForEntity(
                apiUrl(RoutesV2.SUBSCRIPTIONS),
                new HttpEntity<>(subscription),
                SubscriptionRes.class
        );
        assertThat(subscriptionResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        SubscriptionRes createdSubscription = subscriptionResponse.getBody();

        NotificationRes initialNotification = createTestNotification(createdEvent, createdSubscription);

        ResponseEntity<NotificationRes> createResponse = rest.postForEntity(
                apiUrl(RoutesV2.NOTIFICATIONS),
                new HttpEntity<>(initialNotification),
                NotificationRes.class
        );
        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        Long notificationId = createResponse.getBody().getSequenceId();

        // Create updated notification
        NotificationRes updatedNotification = createTestNotification(createdEvent, createdSubscription);
        updatedNotification.setErrorMessage("Updated error message");

        // When
        ResponseEntity<NotificationRes> response = rest.exchange(
                apiUrl(RoutesV2.NOTIFICATIONS, "/" + notificationId),
                HttpMethod.PUT,
                new HttpEntity<>(updatedNotification),
                NotificationRes.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getSequenceId()).isEqualTo(notificationId);
        assertThat(response.getBody().getStatus()).isEqualTo(NotificationStatusRes.PROCESSING);
        assertThat(response.getBody().getErrorMessage()).isEqualTo("Updated error message");

        // Cleanup
        rest.delete(apiUrl(RoutesV2.NOTIFICATIONS, "/" + notificationId));
        rest.delete(apiUrl(RoutesV2.EVENTS, "/" + createdEvent.getSequenceId()));
        rest.delete(apiUrl(RoutesV2.SUBSCRIPTIONS, "/" + createdSubscription.getUuid()));
    }

    @Test
    public void whenDeleteNotificationThenReturnNoContentAndNotificationIsDeleted() {
        // Given - Create event, subscription, and notification
        EventRes event = createTestEvent();
        ResponseEntity<EventRes> eventResponse = rest.postForEntity(
                apiUrl(RoutesV2.EVENTS),
                new HttpEntity<>(event),
                EventRes.class
        );
        assertThat(eventResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        EventRes createdEvent = eventResponse.getBody();

        SubscriptionRes subscription = createTestSubscription();
        ResponseEntity<SubscriptionRes> subscriptionResponse = rest.postForEntity(
                apiUrl(RoutesV2.SUBSCRIPTIONS),
                new HttpEntity<>(subscription),
                SubscriptionRes.class
        );
        assertThat(subscriptionResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        SubscriptionRes createdSubscription = subscriptionResponse.getBody();

        NotificationRes notification = createTestNotification(createdEvent, createdSubscription);

        ResponseEntity<NotificationRes> createResponse = rest.postForEntity(
                apiUrl(RoutesV2.NOTIFICATIONS),
                new HttpEntity<>(notification),
                NotificationRes.class
        );
        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        Long notificationId = createResponse.getBody().getSequenceId();

        // When
        ResponseEntity<Void> response = rest.exchange(
                apiUrl(RoutesV2.NOTIFICATIONS, "/" + notificationId),
                HttpMethod.DELETE,
                null,
                Void.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        // Verify deletion
        ResponseEntity<String> getResponse = rest.getForEntity(
                apiUrl(RoutesV2.NOTIFICATIONS, "/" + notificationId),
                String.class
        );
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

        // Cleanup
        rest.delete(apiUrl(RoutesV2.EVENTS, "/" + createdEvent.getSequenceId()));
        rest.delete(apiUrl(RoutesV2.SUBSCRIPTIONS, "/" + createdSubscription.getUuid()));
    }

    // Helper methods
    private EventRes createTestEvent() {
        EventRes event = new EventRes();
        event.setResourceType("DATA_PRODUCT");
        event.setResourceIdentifier("d5b5b9ac-6a73-4c73-b9ce-4bfc10a1dba0");
        event.setType("DATA_PRODUCT_CREATED");
        event.setEventTypeVersion("1.0.0");
        event.setEventContent(jsonNode("{\"test\": \"content\"}"));
        return event;
    }

    private SubscriptionRes createTestSubscription() {
        SubscriptionRes subscription = new SubscriptionRes();
        subscription.setName("test-subscription");
        subscription.setDisplayName("Test Subscription");
        subscription.setObserverBaseUrl("https://observer.example.com/api/v1");

        List<SubscriptionEventTypeRes> eventTypes = new ArrayList<>();
        SubscriptionEventTypeRes eventType = new SubscriptionEventTypeRes();
        eventType.setEventType("DATA_PRODUCT_CREATED");
        eventTypes.add(eventType);
        subscription.setEventTypes(eventTypes);
        return subscription;
    }

    private NotificationRes createTestNotification(EventRes event, SubscriptionRes subscription) {
        NotificationRes notification = new NotificationRes();
        notification.setEvent(event);
        notification.setSubscription(subscription);
        notification.setStatus(NotificationStatusRes.PROCESSING);
        return notification;
    }
}

