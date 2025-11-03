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
 * Event Controller Integration Tests
 * Simple tests following registry pattern
 */
public class EventControllerIT extends NotificationApplicationIT {

    @Test
    public void whenReadAllEventsThenReturnEventsList() {
        // Given - Create subscription and dispatch event
        SubscriptionRes subscription = new SubscriptionRes();
        subscription.setName("test-read-all-" + System.currentTimeMillis());
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
        assertThat(createSubResponse.getBody()).isNotNull();
        assertThat(createSubResponse.getBody().getUuid()).isNotNull();

        EventRes event = new EventRes();
        event.setResourceType("DATA_PRODUCT");
        event.setResourceIdentifier("dp-read-all");
        event.setType(EventType.DATAPRODUCT_CREATED);
        event.setEventTypeVersion("1.0.0");
        event.setEventContent("{\"id\": \"dp-read-all\"}");

        ResponseEntity<Void> dispatchResponse = rest.postForEntity(
                apiUrl(RoutesV2.DISPATCH),
                new HttpEntity<>(event),
                Void.class
        );
        assertThat(dispatchResponse.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED);

        // When
        ResponseEntity<String> getResponse = rest.getForEntity(
                apiUrl(RoutesV2.EVENTS),
                String.class
        );

        // Then
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(getResponse.getBody()).contains("DATAPRODUCT_CREATED");
        assertThat(getResponse.getBody()).contains("dp-read-all");

        // Cleanup
        rest.delete(apiUrl(RoutesV2.SUBSCRIPTIONS, "/" + createSubResponse.getBody().getUuid()));
    }

    @Test
    public void whenReadOneEventThenReturnEvent() {
        // Given - Create subscription and dispatch event
        SubscriptionRes subscription = new SubscriptionRes();
        subscription.setName("test-read-one-" + System.currentTimeMillis());
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
        assertThat(createSubResponse.getBody()).isNotNull();
        assertThat(createSubResponse.getBody().getUuid()).isNotNull();

        EventRes event = new EventRes();
        event.setResourceType("DATA_PRODUCT");
        event.setResourceIdentifier("dp-single");
        event.setType(EventType.DATAPRODUCT_CREATED);
        event.setEventTypeVersion("1.0.0");
        event.setEventContent("{\"id\": \"dp-single\"}");

        ResponseEntity<Void> dispatchResponse = rest.postForEntity(
                apiUrl(RoutesV2.DISPATCH),
                new HttpEntity<>(event),
                Void.class
        );
        assertThat(dispatchResponse.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED);

        // When - Read events to get the created event ID
        ResponseEntity<String> getEventsResponse = rest.getForEntity(
                apiUrl(RoutesV2.EVENTS),
                String.class
        );
        assertThat(getEventsResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(getEventsResponse.getBody()).contains("dp-single");

        // Cleanup
        rest.delete(apiUrl(RoutesV2.SUBSCRIPTIONS, "/" + createSubResponse.getBody().getUuid()));
    }
}