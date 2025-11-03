package org.opendatamesh.platform.pp.notification.rest.v2.it.controllers;

import org.junit.jupiter.api.Test;
import org.opendatamesh.platform.pp.notification.rest.v2.it.NotificationApplicationIT;
import org.opendatamesh.platform.pp.notification.rest.v2.it.RoutesV2;
import org.opendatamesh.platform.pp.notification.rest.v2.resources.event.EventRes;
import org.opendatamesh.platform.pp.notification.rest.v2.resources.event.EventType;
import org.opendatamesh.platform.pp.notification.rest.v2.resources.notification.NotificationRes;
import org.opendatamesh.platform.pp.notification.rest.v2.resources.subscription.SubscriptionRes;
import org.opendatamesh.platform.pp.notification.rest.v2.resources.subscription.SubscriptionEventTypeRes;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Notification Replay Controller Integration Tests
 * Tests the replay functionality for notifications to a specific subscriber (non-broadcast)
 */
public class NotificationReplayControllerIT extends NotificationApplicationIT {

    @Test
    public void whenReplayNotificationThenNewNotificationIsCreatedAndSentToSpecificSubscriber() {
        // Given - Create subscription and dispatch event
        SubscriptionRes subscription = new SubscriptionRes();
        subscription.setName("test-replay-" + System.currentTimeMillis());
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
        
        String subscriptionUuid = createSubResponse.getBody().getUuid();

        // Dispatch event
        EventRes event = new EventRes();
        event.setResourceType("DATA_PRODUCT");
        event.setResourceIdentifier("dp-replay-test");
        event.setType(EventType.DATAPRODUCT_CREATED);
        event.setEventTypeVersion("1.0.0");
        event.setEventContent("{\"id\": \"dp-replay-test\"}");

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

        // Find the notification that was created for this subscription
        ResponseEntity<String> notificationsResponse = rest.getForEntity(
                apiUrl(RoutesV2.NOTIFICATIONS) + "?subscriptionUuid=" + subscriptionUuid,
                String.class
        );
        assertThat(notificationsResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(notificationsResponse.getBody()).isNotNull();
        
        // Parse the response to get the first notification ID
        ObjectMapper mapper = new ObjectMapper();
        JsonNode notificationsJson;
        try {
            notificationsJson = mapper.readTree(notificationsResponse.getBody());
        } catch (IOException e) {
            throw new RuntimeException("Failed to parse notifications response", e);
        }
        JsonNode content = notificationsJson.get("content");
        assertThat(content).isNotNull();
        assertThat(content.isArray()).isTrue();
        assertThat(content.size()).isGreaterThan(0);
        
        // Get the first notification ID
        Long originalNotificationId = content.get(0).get("sequenceId").asLong();
        assertThat(originalNotificationId).isNotNull();

        // When - Replay the notification
        ResponseEntity<NotificationRes> replayResponse = rest.postForEntity(
                apiUrl(RoutesV2.NOTIFICATIONS, "/" + originalNotificationId + "/replay"),
                new HttpEntity<>(null),
                NotificationRes.class
        );

        // Then - Verify replay response contains complete notification
        assertThat(replayResponse.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED);
        assertThat(replayResponse.getBody()).isNotNull();
        assertThat(replayResponse.getBody().getSequenceId()).isNotNull();
        assertThat(replayResponse.getBody().getSequenceId()).isNotEqualTo(originalNotificationId);
        assertThat(replayResponse.getBody().getSubscription()).isNotNull();
        assertThat(replayResponse.getBody().getSubscription().getUuid()).isEqualTo(subscriptionUuid);
        assertThat(replayResponse.getBody().getEvent()).isNotNull();
        assertThat(replayResponse.getBody().getStatus()).isNotNull();

        // Wait a bit for async processing
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Verify that a new notification was created for the same subscription
        ResponseEntity<String> notificationsAfterReplayResponse = rest.getForEntity(
                apiUrl(RoutesV2.NOTIFICATIONS) + "?subscriptionUuid=" + subscriptionUuid,
                String.class
        );
        assertThat(notificationsAfterReplayResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(notificationsAfterReplayResponse.getBody()).isNotNull();
        
        // Parse to verify there are now more notifications
        JsonNode notificationsAfterReplayJson;
        try {
            notificationsAfterReplayJson = mapper.readTree(notificationsAfterReplayResponse.getBody());
        } catch (IOException e) {
            throw new RuntimeException("Failed to parse notifications after replay response", e);
        }
        JsonNode contentAfterReplay = notificationsAfterReplayJson.get("content");
        assertThat(contentAfterReplay).isNotNull();
        assertThat(contentAfterReplay.isArray()).isTrue();
        assertThat(contentAfterReplay.size()).isGreaterThan(1);

        // Verify that the original notification still exists
        ResponseEntity<NotificationRes> originalNotificationResponse = rest.getForEntity(
                apiUrl(RoutesV2.NOTIFICATIONS, "/" + originalNotificationId),
                NotificationRes.class
        );
        assertThat(originalNotificationResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(originalNotificationResponse.getBody()).isNotNull();
        assertThat(originalNotificationResponse.getBody().getSequenceId()).isEqualTo(originalNotificationId);

        // Cleanup
        rest.delete(apiUrl(RoutesV2.SUBSCRIPTIONS, "/" + subscriptionUuid));
    }

    @Test
    public void whenReplayNonExistentNotificationThenReturnNotFound() {
        // When - Try to replay a notification that doesn't exist
        ResponseEntity<String> replayResponse = rest.postForEntity(
                apiUrl(RoutesV2.NOTIFICATIONS, "/99999/replay"),
                new HttpEntity<>(null),
                String.class
        );

        // Then
        assertThat(replayResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }
}

