package org.opendatamesh.platform.pp.notification.rest.v2.controllers;

import org.junit.jupiter.api.Test;
import org.opendatamesh.platform.pp.notification.rest.NotificationApplicationIT;
import org.opendatamesh.platform.pp.notification.rest.RoutesV2;
import org.opendatamesh.platform.pp.notification.rest.v2.resources.subscription.SubscriptionEventTypeRes;
import org.opendatamesh.platform.pp.notification.rest.v2.resources.subscription.SubscriptionRes;
import org.opendatamesh.platform.pp.notification.rest.v2.resources.subscription.usecases.subscribe.SubscribeCommandRes;
import org.opendatamesh.platform.pp.notification.rest.v2.resources.subscription.usecases.subscribe.SubscribeResponseRes;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

public class SubscriptionUseCaseControllerIT extends NotificationApplicationIT {

    @Test
    public void whenSubscribeObserverWithEventTypesThenReturnSubscription() {
        // Given
        SubscribeCommandRes subscribeCommand = new SubscribeCommandRes();
        subscribeCommand.setName("test-observer-1");
        subscribeCommand.setDisplayName("Test Observer 1");
        subscribeCommand.setObserverBaseUrl("https://observer.example.com/api/v1");
        subscribeCommand.setObserverApiVersion("v1");
        subscribeCommand.setEventTypes(Arrays.asList("DATA_PRODUCT_CREATED", "DATA_PRODUCT_UPDATED"));

        // When
        ResponseEntity<SubscribeResponseRes> response = rest.postForEntity(
                apiUrl(RoutesV2.SUBSCRIPTIONS, "/subscribe"),
                new HttpEntity<>(subscribeCommand),
                SubscribeResponseRes.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        SubscribeResponseRes responseBody = response.getBody();
        assertThat(responseBody.getSubscription()).isNotNull();
        SubscribeResponseRes.Subscription subscription = responseBody.getSubscription();
        assertThat(subscription.getUuid()).isNotNull();
        assertThat(subscription.getName()).isEqualTo(subscribeCommand.getName());
        assertThat(subscription.getDisplayName()).isEqualTo(subscribeCommand.getDisplayName());
        assertThat(subscription.getObserverBaseUrl()).isEqualTo(subscribeCommand.getObserverBaseUrl());
        assertThat(subscription.getObserverApiVersion()).isEqualTo("V1"); // API version is normalized to uppercase
        assertThat(subscription.getEventTypes()).containsExactlyInAnyOrderElementsOf(subscribeCommand.getEventTypes());

        // Cleanup
        rest.delete(apiUrl(RoutesV2.SUBSCRIPTIONS, "/" + subscription.getUuid()));
    }

    @Test
    public void whenSubscribeObserverWithoutEventTypesThenReturnSubscription() {
        // Given
        SubscribeCommandRes subscribeCommand = new SubscribeCommandRes();
        subscribeCommand.setName("test-observer-2");
        subscribeCommand.setDisplayName("Test Observer 2");
        subscribeCommand.setObserverBaseUrl("https://observer.example.com/api/v1");

        // When
        ResponseEntity<SubscribeResponseRes> response = rest.postForEntity(
                apiUrl(RoutesV2.SUBSCRIPTIONS, "/subscribe"),
                new HttpEntity<>(subscribeCommand),
                SubscribeResponseRes.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        SubscribeResponseRes responseBody = response.getBody();
        assertThat(responseBody.getSubscription()).isNotNull();
        SubscribeResponseRes.Subscription subscription = responseBody.getSubscription();
        assertThat(subscription.getUuid()).isNotNull();
        assertThat(subscription.getName()).isEqualTo(subscribeCommand.getName());
        assertThat(subscription.getDisplayName()).isEqualTo(subscribeCommand.getDisplayName());
        assertThat(subscription.getObserverBaseUrl()).isEqualTo(subscribeCommand.getObserverBaseUrl());

        // Cleanup
        rest.delete(apiUrl(RoutesV2.SUBSCRIPTIONS, "/" + subscription.getUuid()));
    }

    @Test
    public void whenSubscribeObserverWithMissingRequiredFieldThenReturnBadRequest() {
        // Given
        SubscribeCommandRes subscribeCommand = new SubscribeCommandRes();
        subscribeCommand.setDisplayName("Test Observer");
        // Missing name and observerBaseUrl

        // When
        ResponseEntity<String> response = rest.postForEntity(
                apiUrl(RoutesV2.SUBSCRIPTIONS, "/subscribe"),
                new HttpEntity<>(subscribeCommand),
                String.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void whenSubscribeExistingObserverWithDifferentEventTypesThenUpdateEventTypes() {
        // Given - Create initial subscription with some event types
        SubscribeCommandRes initialCommand = new SubscribeCommandRes();
        initialCommand.setName("test-observer-3");
        initialCommand.setDisplayName("Test Observer 3");
        initialCommand.setObserverBaseUrl("https://observer.example.com/api/v1");
        initialCommand.setEventTypes(Arrays.asList("DATA_PRODUCT_CREATED", "DATA_PRODUCT_DELETED"));

        ResponseEntity<SubscribeResponseRes> initialResponse = rest.postForEntity(
                apiUrl(RoutesV2.SUBSCRIPTIONS, "/subscribe"),
                new HttpEntity<>(initialCommand),
                SubscribeResponseRes.class
        );
        assertThat(initialResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(initialResponse.getBody()).isNotNull();
        SubscribeResponseRes initialResponseBody = initialResponse.getBody();
        assertThat(initialResponseBody.getSubscription()).isNotNull();
        SubscribeResponseRes.Subscription initialSubscription = initialResponseBody.getSubscription();
        String subscriptionUuid = initialSubscription.getUuid();
        assertThat(initialSubscription.getEventTypes())
                .containsExactlyInAnyOrderElementsOf(initialCommand.getEventTypes());

        // When - Subscribe again with different event types
        SubscribeCommandRes updateCommand = new SubscribeCommandRes();
        updateCommand.setName("test-observer-3");
        updateCommand.setDisplayName("Test Observer 3 Updated");
        updateCommand.setObserverBaseUrl("https://observer.example.com/api/v1");
        updateCommand.setEventTypes(Arrays.asList("DATA_PRODUCT_CREATED", "DATA_PRODUCT_UPDATED"));

        ResponseEntity<SubscribeResponseRes> updateResponse = rest.postForEntity(
                apiUrl(RoutesV2.SUBSCRIPTIONS, "/subscribe"),
                new HttpEntity<>(updateCommand),
                SubscribeResponseRes.class
        );

        // Then
        assertThat(updateResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(updateResponse.getBody()).isNotNull();
        SubscribeResponseRes updateResponseBody = updateResponse.getBody();
        assertThat(updateResponseBody.getSubscription()).isNotNull();
        SubscribeResponseRes.Subscription updatedSubscription = updateResponseBody.getSubscription();
        assertThat(updatedSubscription.getUuid()).isEqualTo(subscriptionUuid);
        assertThat(updatedSubscription.getName()).isEqualTo(updateCommand.getName());
        assertThat(updatedSubscription.getDisplayName()).isEqualTo(updateCommand.getDisplayName());
        // Verify event types: should have CREATED (kept), UPDATED (added), and not have DELETED (removed)
        assertThat(updatedSubscription.getEventTypes())
                .containsExactlyInAnyOrderElementsOf(updateCommand.getEventTypes());
        assertThat(updatedSubscription.getEventTypes()).doesNotContain("DATA_PRODUCT_DELETED");

        // Cleanup
        rest.delete(apiUrl(RoutesV2.SUBSCRIPTIONS, "/" + subscriptionUuid));
    }

    @Test
    public void whenSubscribeObserverWithEventTypesThenFetchSubscriptionHasEventTypes() {
        // Given - Create subscription with event types using subscribe endpoint
        SubscribeCommandRes subscribeCommand = new SubscribeCommandRes();
        subscribeCommand.setName("test-observer-4");
        subscribeCommand.setDisplayName("Test Observer 4");
        subscribeCommand.setObserverBaseUrl("https://observer.example.com/api/v1");
        subscribeCommand.setObserverApiVersion("v1");
        List<String> expectedEventTypes = Arrays.asList("DATA_PRODUCT_CREATED", "DATA_PRODUCT_UPDATED", "DATA_PRODUCT_DELETED");
        subscribeCommand.setEventTypes(expectedEventTypes);

        ResponseEntity<SubscribeResponseRes> subscribeResponse = rest.postForEntity(
                apiUrl(RoutesV2.SUBSCRIPTIONS, "/subscribe"),
                new HttpEntity<>(subscribeCommand),
                SubscribeResponseRes.class
        );
        assertThat(subscribeResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(subscribeResponse.getBody()).isNotNull();
        assertThat(subscribeResponse.getBody().getSubscription()).isNotNull();
        String subscriptionUuid = subscribeResponse.getBody().getSubscription().getUuid();
        assertThat(subscriptionUuid).isNotNull();

        // When - Fetch the subscription using the GET endpoint
        ResponseEntity<SubscriptionRes> getResponse = rest.getForEntity(
                apiUrl(RoutesV2.SUBSCRIPTIONS, "/" + subscriptionUuid),
                SubscriptionRes.class
        );

        // Then - Verify that the fetched subscription has the expected event types
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(getResponse.getBody()).isNotNull();
        SubscriptionRes fetchedSubscription = getResponse.getBody();
        assertThat(fetchedSubscription.getUuid()).isEqualTo(subscriptionUuid);
        assertThat(fetchedSubscription.getEventTypes()).isNotNull();
        assertThat(fetchedSubscription.getEventTypes()).hasSize(expectedEventTypes.size());
        
        // Extract event type strings from SubscriptionEventTypeRes objects
        List<String> fetchedEventTypes = fetchedSubscription.getEventTypes().stream()
                .map(SubscriptionEventTypeRes::getEventType)
                .collect(Collectors.toList());
        assertThat(fetchedEventTypes).containsExactlyInAnyOrderElementsOf(expectedEventTypes);

        // Cleanup
        rest.delete(apiUrl(RoutesV2.SUBSCRIPTIONS, "/" + subscriptionUuid));
    }
}

