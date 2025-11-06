package org.opendatamesh.platform.pp.notification.rest.v2.controllers;

import org.junit.jupiter.api.Test;
import org.opendatamesh.platform.pp.notification.rest.NotificationApplicationIT;
import org.opendatamesh.platform.pp.notification.rest.RoutesV2;
import org.opendatamesh.platform.pp.notification.rest.v2.resources.subscription.SubscriptionEventTypeRes;
import org.opendatamesh.platform.pp.notification.rest.v2.resources.subscription.SubscriptionRes;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class SubscriptionControllerIT extends NotificationApplicationIT {

    @Test
    public void whenCreateSubscriptionThenReturnCreatedSubscription() {
        // Given
        SubscriptionRes subscription = new SubscriptionRes();
        subscription.setName("test-subscription");
        subscription.setDisplayName("Test Subscription");
        subscription.setObserverServerBaseUrl("https://observer.example.com/api/v1");

        List<SubscriptionEventTypeRes> eventTypes = new ArrayList<>();
        SubscriptionEventTypeRes eventType = new SubscriptionEventTypeRes();
        eventType.setEventType("DATA_PRODUCT_CREATED");
        eventTypes.add(eventType);
        subscription.setEventTypes(eventTypes);

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
        assertThat(response.getBody().getDisplayName()).isEqualTo(subscription.getDisplayName());
        assertThat(response.getBody().getObserverApiVersion()).isEqualTo("V1"); // Default value

        // Cleanup
        rest.delete(apiUrl(RoutesV2.SUBSCRIPTIONS, "/" + response.getBody().getUuid()));
    }

    @Test
    public void whenGetSubscriptionByIdThenReturnSubscription() {
        // Given - Create and save subscription
        SubscriptionRes subscription = new SubscriptionRes();
        subscription.setName("test-subscription");
        subscription.setDisplayName("Test Subscription");
        subscription.setObserverServerBaseUrl("https://observer.example.com/api/v1");

        List<SubscriptionEventTypeRes> eventTypes = new ArrayList<>();
        SubscriptionEventTypeRes eventType = new SubscriptionEventTypeRes();
        eventType.setEventType("DATA_PRODUCT_CREATED");
        eventTypes.add(eventType);
        subscription.setEventTypes(eventTypes);

        ResponseEntity<SubscriptionRes> createResponse = rest.postForEntity(
                apiUrl(RoutesV2.SUBSCRIPTIONS),
                new HttpEntity<>(subscription),
                SubscriptionRes.class
        );
        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        String subscriptionId = createResponse.getBody().getUuid();

        // When
        ResponseEntity<SubscriptionRes> response = rest.getForEntity(
                apiUrl(RoutesV2.SUBSCRIPTIONS, "/" + subscriptionId),
                SubscriptionRes.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getUuid()).isEqualTo(subscriptionId);
        assertThat(response.getBody().getName()).isEqualTo(subscription.getName());
        assertThat(response.getBody().getObserverApiVersion()).isEqualTo("V1"); // Default value

        // Cleanup
        rest.delete(apiUrl(RoutesV2.SUBSCRIPTIONS, "/" + subscriptionId));
    }

    @Test
    public void whenGetSubscriptionWithNonExistentIdThenReturnNotFound() {
        // Given
        String nonExistentId = "non-existent-uuid";

        // When
        ResponseEntity<String> response = rest.getForEntity(
                apiUrl(RoutesV2.SUBSCRIPTIONS, "/" + nonExistentId),
                String.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    public void whenSearchSubscriptionsThenReturnSubscriptionsList() {
        // Given - Create and save first subscription
        SubscriptionRes subscription1 = new SubscriptionRes();
        subscription1.setName("test-subscription-1");
        subscription1.setDisplayName("Test Subscription 1");
        subscription1.setObserverServerBaseUrl("https://observer1.example.com/api/v1");

        List<SubscriptionEventTypeRes> eventTypes1 = new ArrayList<>();
        SubscriptionEventTypeRes eventType1 = new SubscriptionEventTypeRes();
        eventType1.setEventType("DATA_PRODUCT_CREATED");
        eventTypes1.add(eventType1);
        subscription1.setEventTypes(eventTypes1);

        ResponseEntity<SubscriptionRes> createResponse1 = rest.postForEntity(
                apiUrl(RoutesV2.SUBSCRIPTIONS),
                new HttpEntity<>(subscription1),
                SubscriptionRes.class
        );
        assertThat(createResponse1.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        String subscription1Id = createResponse1.getBody().getUuid();

        // Create and save second subscription
        SubscriptionRes subscription2 = new SubscriptionRes();
        subscription2.setName("test-subscription-2");
        subscription2.setDisplayName("Test Subscription 2");
        subscription2.setObserverServerBaseUrl("https://observer2.example.com/api/v1");

        List<SubscriptionEventTypeRes> eventTypes2 = new ArrayList<>();
        SubscriptionEventTypeRes eventType2 = new SubscriptionEventTypeRes();
        eventType2.setEventType("DATA_PRODUCT_UPDATED");
        eventTypes2.add(eventType2);
        subscription2.setEventTypes(eventTypes2);

        ResponseEntity<SubscriptionRes> createResponse2 = rest.postForEntity(
                apiUrl(RoutesV2.SUBSCRIPTIONS),
                new HttpEntity<>(subscription2),
                SubscriptionRes.class
        );
        assertThat(createResponse2.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        String subscription2Id = createResponse2.getBody().getUuid();

        // When
        ResponseEntity<String> response = rest.getForEntity(
                apiUrl(RoutesV2.SUBSCRIPTIONS),
                String.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();

        // Cleanup
        rest.delete(apiUrl(RoutesV2.SUBSCRIPTIONS, "/" + subscription1Id));
        rest.delete(apiUrl(RoutesV2.SUBSCRIPTIONS, "/" + subscription2Id));
    }

    @Test
    public void whenUpdateSubscriptionThenReturnUpdatedSubscription() {
        // Given - Create and save initial subscription
        SubscriptionRes initialSubscription = new SubscriptionRes();
        initialSubscription.setName("test-subscription");
        initialSubscription.setDisplayName("Initial Display Name");
        initialSubscription.setObserverServerBaseUrl("https://observer.example.com/api/v1");

        List<SubscriptionEventTypeRes> eventTypes = new ArrayList<>();
        SubscriptionEventTypeRes eventType = new SubscriptionEventTypeRes();
        eventType.setEventType("DATA_PRODUCT_CREATED");
        eventTypes.add(eventType);
        initialSubscription.setEventTypes(eventTypes);

        ResponseEntity<SubscriptionRes> createResponse = rest.postForEntity(
                apiUrl(RoutesV2.SUBSCRIPTIONS),
                new HttpEntity<>(initialSubscription),
                SubscriptionRes.class
        );
        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        String subscriptionId = createResponse.getBody().getUuid();

        // Create updated subscription
        SubscriptionRes updatedSubscription = new SubscriptionRes();
        updatedSubscription.setName("test-subscription");
        updatedSubscription.setDisplayName("Updated Display Name");
        updatedSubscription.setObserverServerBaseUrl("https://observer-updated.example.com/api/v1");

        List<SubscriptionEventTypeRes> updatedEventTypes = new ArrayList<>();
        SubscriptionEventTypeRes updatedEventType = new SubscriptionEventTypeRes();
        updatedEventType.setEventType("DATA_PRODUCT_UPDATED");
        updatedEventTypes.add(updatedEventType);
        updatedSubscription.setEventTypes(updatedEventTypes);

        // When
        ResponseEntity<SubscriptionRes> response = rest.exchange(
                apiUrl(RoutesV2.SUBSCRIPTIONS, "/" + subscriptionId),
                HttpMethod.PUT,
                new HttpEntity<>(updatedSubscription),
                SubscriptionRes.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getUuid()).isEqualTo(subscriptionId);
        assertThat(response.getBody().getDisplayName()).isEqualTo("Updated Display Name");
        assertThat(response.getBody().getObserverServerBaseUrl()).isEqualTo("https://observer-updated.example.com/api/v1");
        assertThat(response.getBody().getObserverApiVersion()).isEqualTo("V1"); // Default value

        // Cleanup
        rest.delete(apiUrl(RoutesV2.SUBSCRIPTIONS, "/" + subscriptionId));
    }

    @Test
    public void whenCreateSubscriptionWithObserverApiVersionThenReturnCreatedSubscriptionWithProvidedVersion() {
        // Given
        SubscriptionRes subscription = new SubscriptionRes();
        subscription.setName("test-subscription-with-version");
        subscription.setDisplayName("Test Subscription With Version");
        subscription.setObserverServerBaseUrl("https://observer.example.com/api/v2");
        subscription.setObserverApiVersion("v2");

        List<SubscriptionEventTypeRes> eventTypes = new ArrayList<>();
        SubscriptionEventTypeRes eventType = new SubscriptionEventTypeRes();
        eventType.setEventType("DATA_PRODUCT_CREATED");
        eventTypes.add(eventType);
        subscription.setEventTypes(eventTypes);

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
        assertThat(response.getBody().getDisplayName()).isEqualTo(subscription.getDisplayName());
        assertThat(response.getBody().getObserverApiVersion()).isEqualTo("V2"); // Provided value

        // Cleanup
        rest.delete(apiUrl(RoutesV2.SUBSCRIPTIONS, "/" + response.getBody().getUuid()));
    }

    @Test
    public void whenDeleteSubscriptionThenReturnNoContentAndSubscriptionIsDeleted() {
        // Given - Create and save subscription
        SubscriptionRes subscription = new SubscriptionRes();
        subscription.setName("test-subscription");
        subscription.setDisplayName("Test Subscription");
        subscription.setObserverServerBaseUrl("https://observer.example.com/api/v1");

        List<SubscriptionEventTypeRes> eventTypes = new ArrayList<>();
        SubscriptionEventTypeRes eventType = new SubscriptionEventTypeRes();
        eventType.setEventType("DATA_PRODUCT_CREATED");
        eventTypes.add(eventType);
        subscription.setEventTypes(eventTypes);

        ResponseEntity<SubscriptionRes> createResponse = rest.postForEntity(
                apiUrl(RoutesV2.SUBSCRIPTIONS),
                new HttpEntity<>(subscription),
                SubscriptionRes.class
        );
        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        String subscriptionId = createResponse.getBody().getUuid();

        // When
        ResponseEntity<Void> response = rest.exchange(
                apiUrl(RoutesV2.SUBSCRIPTIONS, "/" + subscriptionId),
                HttpMethod.DELETE,
                null,
                Void.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        // Verify deletion
        ResponseEntity<String> getResponse = rest.getForEntity(
                apiUrl(RoutesV2.SUBSCRIPTIONS, "/" + subscriptionId),
                String.class
        );
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }
}

