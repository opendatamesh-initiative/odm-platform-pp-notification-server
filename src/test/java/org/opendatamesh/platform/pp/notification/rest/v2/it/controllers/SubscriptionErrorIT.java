package org.opendatamesh.platform.pp.notification.rest.v2.it.controllers;

import org.junit.jupiter.api.Test;
import org.opendatamesh.platform.pp.notification.rest.v2.it.NotificationApplicationIT;
import org.opendatamesh.platform.pp.notification.rest.v2.it.RoutesV2;
import org.opendatamesh.platform.pp.notification.rest.v2.resources.subscription.SubscriptionRes;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Error handling tests for Subscription operations
 */
public class SubscriptionErrorIT extends NotificationApplicationIT {

    @Test
    public void testCreateSubscriptionWithInvalidData_ReturnsBadRequest() {
        // Given - Missing required fields
        SubscriptionRes invalidSubscription = new SubscriptionRes();
        invalidSubscription.setName("test-invalid");

        // When
        ResponseEntity<String> response = rest.postForEntity(
                apiUrl(RoutesV2.SUBSCRIPTIONS),
                new HttpEntity<>(invalidSubscription),
                String.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void testGetNonExistentSubscription_ReturnsNotFound() {
        // Given
        String nonExistentUuid = "non-existent-uuid";

        // When
        ResponseEntity<String> response = rest.getForEntity(
                apiUrl(RoutesV2.SUBSCRIPTIONS, "/" + nonExistentUuid),
                String.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    public void testUpdateNonExistentSubscription_ReturnsNotFound() {
        // Given
        String nonExistentUuid = "non-existent-uuid";
        SubscriptionRes subscription = new SubscriptionRes();
        subscription.setName("test-update");
        subscription.setObserverServerBaseUrl("http://localhost:8080");

        // When
        ResponseEntity<String> response = rest.exchange(
                apiUrl(RoutesV2.SUBSCRIPTIONS, "/" + nonExistentUuid),
                org.springframework.http.HttpMethod.PUT,
                new HttpEntity<>(subscription),
                String.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    public void testDeleteNonExistentSubscription_ReturnsNotFound() {
        // Given
        String nonExistentUuid = "non-existent-uuid";

        // When
        ResponseEntity<Void> response = rest.exchange(
                apiUrl(RoutesV2.SUBSCRIPTIONS, "/" + nonExistentUuid),
                org.springframework.http.HttpMethod.DELETE,
                null,
                Void.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }
}

