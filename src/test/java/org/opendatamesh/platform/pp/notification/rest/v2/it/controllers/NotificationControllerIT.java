package org.opendatamesh.platform.pp.notification.rest.v2.it.controllers;

import org.junit.jupiter.api.Test;
import org.opendatamesh.platform.pp.notification.rest.v2.it.NotificationApplicationIT;
import org.opendatamesh.platform.pp.notification.rest.v2.it.RoutesV2;
import org.opendatamesh.platform.pp.notification.rest.v2.resources.event.EventRes;
import org.opendatamesh.platform.pp.notification.rest.v2.resources.event.EventType;
import org.opendatamesh.platform.pp.notification.rest.v2.resources.subscription.SubscriptionRes;
import org.opendatamesh.platform.pp.notification.rest.v2.resources.subscription.SubscriptionEventTypeRes;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Notification Controller Integration Tests
 * Simple tests following registry pattern
 */
public class NotificationControllerIT extends NotificationApplicationIT {

    @Test
    public void whenDispatchEventThenNotificationIsCreated() {
        // Given - Create subscription
        SubscriptionRes subscription = new SubscriptionRes();
        subscription.setName("test-notifications-" + System.currentTimeMillis());
        subscription.setObserverServerBaseUrl("http://localhost:8080/test");
        SubscriptionEventTypeRes eventType = new SubscriptionEventTypeRes();
        eventType.setEventName("DATAPRODUCT_CREATED");
        subscription.setEventTypes(Arrays.asList(eventType));

        ResponseEntity<SubscriptionRes> createSubResponse = rest.postForEntity(
                apiUrl(RoutesV2.SUBSCRIPTIONS),
                new HttpEntity<>(subscription),
                SubscriptionRes.class
        );
        assertThat(createSubResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        // Dispatch event
        EventRes event = new EventRes();
        event.setResourceType("DATA_PRODUCT");
        event.setResourceIdentifier("dp-notification-test");
        event.setType(EventType.DATAPRODUCT_CREATED);
        event.setEventTypeVersion("1.0.0");
        event.setEventContent("{\"id\": \"dp-notification-test\"}");

        ResponseEntity<Void> dispatchResponse = rest.postForEntity(
                apiUrl(RoutesV2.DISPATCH),
                new HttpEntity<>(event),
                Void.class
        );
        assertThat(dispatchResponse.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED);

        // Wait a bit for async processing
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // When - Read notifications
        ResponseEntity<String> getResponse = rest.getForEntity(
                apiUrl(RoutesV2.NOTIFICATIONS),
                String.class
        );

        // Then
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(getResponse.getBody()).isNotNull();
        assertThat(getResponse.getBody()).contains("DATAPRODUCT_CREATED");

        // Cleanup
        rest.delete(apiUrl(RoutesV2.SUBSCRIPTIONS, "/" + createSubResponse.getBody().getUuid()));
    }

    @Test
    public void whenReadAllNotificationsThenReturnNotificationsList() {
        // Given - Create subscription and dispatch event
        SubscriptionRes subscription = new SubscriptionRes();
        subscription.setName("test-read-notifications-" + System.currentTimeMillis());
        subscription.setObserverServerBaseUrl("http://localhost:8080/test");
        SubscriptionEventTypeRes eventType = new SubscriptionEventTypeRes();
        eventType.setEventName("DATAPRODUCT_CREATED");
        subscription.setEventTypes(Arrays.asList(eventType));

        ResponseEntity<SubscriptionRes> createSubResponse = rest.postForEntity(
                apiUrl(RoutesV2.SUBSCRIPTIONS),
                new HttpEntity<>(subscription),
                SubscriptionRes.class
        );

        EventRes event = new EventRes();
        event.setResourceType("DATA_PRODUCT");
        event.setResourceIdentifier("dp-read-notifications");
        event.setType(EventType.DATAPRODUCT_CREATED);
        event.setEventTypeVersion("1.0.0");
        event.setEventContent("{\"id\": \"dp-read-notifications\"}");

        ResponseEntity<Void> dispatchResponse = rest.postForEntity(
                apiUrl(RoutesV2.DISPATCH),
                new HttpEntity<>(event),
                Void.class
        );
        assertThat(dispatchResponse.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED);

        // Wait a bit for async processing
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // When
        ResponseEntity<String> getResponse = rest.getForEntity(
                apiUrl(RoutesV2.NOTIFICATIONS),
                String.class
        );

        // Then
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(getResponse.getBody()).isNotNull();

        // Cleanup
        rest.delete(apiUrl(RoutesV2.SUBSCRIPTIONS, "/" + createSubResponse.getBody().getUuid()));
    }
}