package org.opendatamesh.platform.pp.notification.rest.v2.it.controllers;

import org.junit.jupiter.api.Test;
import org.opendatamesh.platform.pp.notification.rest.v2.it.NotificationApplicationIT;
import org.opendatamesh.platform.pp.notification.rest.v2.it.RoutesV2;
import org.opendatamesh.platform.pp.notification.rest.v2.resources.subscription.SubscriptionRes;
import org.opendatamesh.platform.pp.notification.rest.v2.resources.subscription.SubscriptionEventTypeRes;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Acceptance Criteria 1 & 2: Service can subscribe to events / to a specific set of events
 */
public class SubscriptionControllerIT extends NotificationApplicationIT {

    @Test
    public void whenCreateSubscriptionWithAllEventTypesThenReturnCreatedSubscription() {
        // Given
        SubscriptionRes subscription = new SubscriptionRes();
        subscription.setName("test-subscription-all-" + System.currentTimeMillis());
        subscription.setDisplayName("Test Subscription All Events");
        subscription.setObserverServerBaseUrl("http://localhost:8080/test-observer");
        // Subscribe to ALL events by not setting eventTypes

        // When
        ResponseEntity<SubscriptionRes> response = rest.postForEntity(
                apiUrl(RoutesV2.SUBSCRIPTIONS),
                new HttpEntity<>(subscription),
                SubscriptionRes.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getUuid()).isNotNull();
        assertThat(response.getBody().getName()).isEqualTo(subscription.getName());
        assertThat(response.getBody().getObserverServerBaseUrl()).isEqualTo("http://localhost:8080/test-observer");

        // Cleanup
        rest.delete(apiUrl(RoutesV2.SUBSCRIPTIONS, "/" + response.getBody().getUuid()));
    }

    @Test
    public void whenCreateSubscriptionWithSpecificEventTypesThenReturnCreatedSubscription() {
        // Given - AC2: Subscribe to specific events
        SubscriptionRes subscription = new SubscriptionRes();
        subscription.setName("test-subscription-specific-" + System.currentTimeMillis());
        subscription.setDisplayName("Test Subscription Specific Events");
        subscription.setObserverServerBaseUrl("http://localhost:8080/test-observer");
        
        // Subscribe to specific event types
        SubscriptionEventTypeRes eventType1 = new SubscriptionEventTypeRes();
        eventType1.setEventName("DATAPRODUCT_CREATED");
        SubscriptionEventTypeRes eventType2 = new SubscriptionEventTypeRes();
        eventType2.setEventName("DATAPRODUCT_UPDATED");
        subscription.setEventTypes(Arrays.asList(eventType1, eventType2));

        // When
        ResponseEntity<SubscriptionRes> response = rest.postForEntity(
                apiUrl(RoutesV2.SUBSCRIPTIONS),
                new HttpEntity<>(subscription),
                SubscriptionRes.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getUuid()).isNotNull();
        List<String> eventTypeNames = response.getBody().getEventTypes().stream()
                .map(SubscriptionEventTypeRes::getEventName)
                .collect(Collectors.toList());
        assertThat(eventTypeNames).contains("DATAPRODUCT_CREATED");
        assertThat(eventTypeNames).contains("DATAPRODUCT_UPDATED");

        // Cleanup
        rest.delete(apiUrl(RoutesV2.SUBSCRIPTIONS, "/" + response.getBody().getUuid()));
    }

    @Test
    public void whenCreateSubscriptionWithInvalidDataThenReturnBadRequest() {
        // Given
        SubscriptionRes invalidSubscription = new SubscriptionRes();
        // Missing required fields

        // When
        ResponseEntity<String> response = rest.postForEntity(
                apiUrl(RoutesV2.SUBSCRIPTIONS),
                new HttpEntity<>(invalidSubscription),
                String.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }
}

