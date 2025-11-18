package org.opendatamesh.platform.pp.notification.rest.v2.controllers;

import org.junit.jupiter.api.Test;
import org.opendatamesh.platform.pp.notification.rest.NotificationApplicationIT;
import org.opendatamesh.platform.pp.notification.rest.RoutesV2;
import org.opendatamesh.platform.pp.notification.rest.v2.resources.subscription.usecases.subscribe.SubscribeCommandRes;
import org.opendatamesh.platform.pp.notification.rest.v2.resources.subscription.usecases.subscribe.SubscribeResponseRes;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

public class SubscriptionUseCaseControllerIT extends NotificationApplicationIT {

    @Test
    public void whenSubscribeObserverWithEventTypesThenReturnSubscription() {
        // Given
        SubscribeCommandRes subscribeCommand = new SubscribeCommandRes();
        subscribeCommand.setObserverName("test-observer-1");
        subscribeCommand.setObserverDisplayName("Test Observer 1");
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
        assertThat(subscription.getObserverName()).isEqualTo(subscribeCommand.getObserverName());
        assertThat(subscription.getObserverDisplayName()).isEqualTo(subscribeCommand.getObserverDisplayName());
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
        subscribeCommand.setObserverName("test-observer-2");
        subscribeCommand.setObserverDisplayName("Test Observer 2");
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
        assertThat(subscription.getObserverName()).isEqualTo(subscribeCommand.getObserverName());
        assertThat(subscription.getObserverDisplayName()).isEqualTo(subscribeCommand.getObserverDisplayName());
        assertThat(subscription.getObserverBaseUrl()).isEqualTo(subscribeCommand.getObserverBaseUrl());

        // Cleanup
        rest.delete(apiUrl(RoutesV2.SUBSCRIPTIONS, "/" + subscription.getUuid()));
    }

    @Test
    public void whenSubscribeObserverWithMissingRequiredFieldThenReturnBadRequest() {
        // Given
        SubscribeCommandRes subscribeCommand = new SubscribeCommandRes();
        subscribeCommand.setObserverDisplayName("Test Observer");
        // Missing observerName and observerBaseUrl

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
        initialCommand.setObserverName("test-observer-3");
        initialCommand.setObserverDisplayName("Test Observer 3");
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
        updateCommand.setObserverName("test-observer-3");
        updateCommand.setObserverDisplayName("Test Observer 3 Updated");
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
        assertThat(updatedSubscription.getObserverName()).isEqualTo(updateCommand.getObserverName());
        assertThat(updatedSubscription.getObserverDisplayName()).isEqualTo(updateCommand.getObserverDisplayName());
        // Verify event types: should have CREATED (kept), UPDATED (added), and not have DELETED (removed)
        assertThat(updatedSubscription.getEventTypes())
                .containsExactlyInAnyOrderElementsOf(updateCommand.getEventTypes());
        assertThat(updatedSubscription.getEventTypes()).doesNotContain("DATA_PRODUCT_DELETED");

        // Cleanup
        rest.delete(apiUrl(RoutesV2.SUBSCRIPTIONS, "/" + subscriptionUuid));
    }
}

