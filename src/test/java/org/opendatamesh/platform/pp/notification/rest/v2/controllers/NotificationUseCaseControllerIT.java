package org.opendatamesh.platform.pp.notification.rest.v2.controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.opendatamesh.platform.pp.notification.client.ObserverClient;
import org.opendatamesh.platform.pp.notification.notification.entities.Notification;
import org.opendatamesh.platform.pp.notification.rest.NotificationApplicationIT;
import org.opendatamesh.platform.pp.notification.rest.RoutesV2;
import org.opendatamesh.platform.pp.notification.rest.v2.resources.event.usecases.emit.EventEmitCommandRes;
import org.opendatamesh.platform.pp.notification.rest.v2.resources.event.usecases.emit.EventEmitResponseRes;
import org.opendatamesh.platform.pp.notification.rest.v2.resources.notification.NotificationRes;
import org.opendatamesh.platform.pp.notification.rest.v2.resources.notification.NotificationStatusRes;
import org.opendatamesh.platform.pp.notification.rest.v2.resources.notification.usecases.replay.NotificationReplayCommandRes;
import org.opendatamesh.platform.pp.notification.rest.v2.resources.notification.usecases.replay.NotificationReplayResponseRes;
import org.opendatamesh.platform.pp.notification.rest.v2.resources.subscription.SubscriptionRes;
import org.opendatamesh.platform.pp.notification.rest.v2.resources.subscription.usecases.subscribe.SubscribeCommandRes;
import org.opendatamesh.platform.pp.notification.rest.v2.resources.subscription.usecases.subscribe.SubscribeResponseRes;
import org.opendatamesh.platform.pp.notification.utils.client.jackson.PageUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class NotificationUseCaseControllerIT extends NotificationApplicationIT {

    @Autowired
    private ObserverClient observerClient;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    public void setUp() {
        reset(observerClient);
    }

    @AfterEach
    public void tearDown() {
        reset(observerClient);
    }

    @Test
    public void whenReplayFailedNotificationThenEventIsDispatchedToObserverAndStatusIsUpdated() {
        // Given - Subscribe to an event type
        String observerName = "test-observer-replay-" + System.currentTimeMillis();
        SubscribeCommandRes subscribeCommand = new SubscribeCommandRes();
        subscribeCommand.setName(observerName);
        subscribeCommand.setDisplayName("Test Observer for Replay");
        subscribeCommand.setObserverBaseUrl("https://observer-replay.example.com/api/v1");
        subscribeCommand.setObserverApiVersion("v1");
        subscribeCommand.setEventTypes(Arrays.asList("DATA_PRODUCT_CREATED"));

        ResponseEntity<SubscribeResponseRes> subscriptionResponse = rest.postForEntity(
                apiUrl(RoutesV2.SUBSCRIPTIONS, "/subscribe"),
                new HttpEntity<>(subscribeCommand),
                SubscribeResponseRes.class
        );
        assertThat(subscriptionResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(subscriptionResponse.getBody()).isNotNull();
        SubscribeResponseRes subscriptionResponseBody = subscriptionResponse.getBody();
        assertThat(subscriptionResponseBody.getSubscription()).isNotNull();
        SubscribeResponseRes.Subscription subscription = subscriptionResponseBody.getSubscription();
        String subscriptionUuid = subscription.getUuid();

        // Configure mock to throw exception so notification fails
        doThrow(new RuntimeException("TEST EXCEPTION!!!: Connection timeout while reaching observer server"))
                .when(observerClient).dispatchNotification(any(Notification.class));

        // Emit an event
        EventEmitCommandRes.Event event = new EventEmitCommandRes.Event();
        event.setResourceType("DATA_PRODUCT");
        event.setResourceIdentifier("d5b5b9ac-6a73-4c73-b9ce-4bfc10a1dba0");
        event.setType("DATA_PRODUCT_CREATED");
        event.setEventTypeVersion("1.0.0");
        event.setEventContent(jsonNode("{\"test\": \"content\"}"));

        EventEmitCommandRes emitCommand = new EventEmitCommandRes(event);

        ResponseEntity<EventEmitResponseRes> emitResponse = rest.postForEntity(
                apiUrl(RoutesV2.EVENTS, "/emit"),
                new HttpEntity<>(emitCommand),
                EventEmitResponseRes.class
        );
        assertThat(emitResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(emitResponse.getBody()).isNotNull();
        EventEmitResponseRes emitResponseBody = emitResponse.getBody();
        assertThat(emitResponseBody.getEvent()).isNotNull();
        EventEmitResponseRes.Event createdEvent = emitResponseBody.getEvent();
        Long eventId = createdEvent.getSequenceId();

        // Search notifications for the given observer name
        // First, find subscription by observer name
        ResponseEntity<PageUtility<SubscriptionRes>> subscriptionSearchResponse = rest.exchange(
                apiUrl(RoutesV2.SUBSCRIPTIONS) + "?name=" + observerName,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );
        assertThat(subscriptionSearchResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(subscriptionSearchResponse.getBody()).isNotNull();
        PageUtility<SubscriptionRes> subscriptionSearchBody = subscriptionSearchResponse.getBody();
        Page<SubscriptionRes> subscriptions = subscriptionSearchBody;
        assertThat(subscriptions.getContent()).hasSize(1);
        String foundSubscriptionUuid = subscriptions.getContent().get(0).getUuid();
        assertThat(foundSubscriptionUuid).isEqualTo(subscriptionUuid);

        // Search notifications by subscription UUID and status FAILED_TO_DELIVER
        ResponseEntity<PageUtility<NotificationRes>> notificationsResponse = rest.exchange(
                apiUrl(RoutesV2.NOTIFICATIONS) + "?subscriptionUuid=" + subscriptionUuid + "&notificationStatus=FAILED_TO_DELIVER&sort=createdAt,desc",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );
        assertThat(notificationsResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(notificationsResponse.getBody()).isNotNull();
        PageUtility<NotificationRes> notificationsResponseBody = notificationsResponse.getBody();
        Page<NotificationRes> notifications = notificationsResponseBody;
        assertThat(notifications.getContent()).isNotEmpty();

        // Select the last created notification that has status Failed to deliver
        List<NotificationRes> failedNotifications = notifications.getContent();
        NotificationRes failedNotification = failedNotifications.get(0); // Already sorted by createdAt desc
        assertThat(failedNotification.getStatus()).isEqualTo(NotificationStatusRes.FAILED_TO_DELIVER);
        assertThat(failedNotification.getEvent()).isNotNull();
        assertThat(failedNotification.getEvent().getSequenceId()).isEqualTo(eventId);
        assertThat(failedNotification.getSubscription()).isNotNull();
        assertThat(failedNotification.getSubscription().getUuid()).isEqualTo(subscriptionUuid);

        Long notificationSequenceId = failedNotification.getSequenceId();

        // Reset mock to allow successful dispatch on replay
        reset(observerClient);

        // When - Replay the notification
        NotificationReplayCommandRes replayCommand = new NotificationReplayCommandRes();
        replayCommand.setNotificationSequenceId(notificationSequenceId);

        ResponseEntity<NotificationReplayResponseRes> replayResponse = rest.postForEntity(
                apiUrl(RoutesV2.NOTIFICATIONS, "/replay"),
                new HttpEntity<>(replayCommand),
                NotificationReplayResponseRes.class
        );

        // Then
        assertThat(replayResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(replayResponse.getBody()).isNotNull();
        NotificationReplayResponseRes replayResponseBody = replayResponse.getBody();
        assertThat(replayResponseBody.getNotification()).isNotNull();
        NotificationReplayResponseRes.Notification replayedNotification = replayResponseBody.getNotification();
        assertThat(replayedNotification.getSequenceId()).isEqualTo(notificationSequenceId);

        // Ensure the event is dispatched only to the observer associated to the replayed notification
        ArgumentCaptor<Notification> notificationCaptor = ArgumentCaptor.forClass(Notification.class);
        verify(observerClient, times(1)).dispatchNotification(notificationCaptor.capture());

        Notification dispatchedNotification = notificationCaptor.getValue();
        assertThat(dispatchedNotification).isNotNull();
        assertThat(dispatchedNotification.getSequenceId()).isEqualTo(notificationSequenceId);
        assertThat(dispatchedNotification.getSubscription()).isNotNull();
        assertThat(dispatchedNotification.getSubscription().getUuid()).isEqualTo(subscriptionUuid);
        assertThat(dispatchedNotification.getSubscription().getName()).isEqualTo(observerName);

        // Ensure the event is the same contained in the replayed notification
        assertThat(dispatchedNotification.getEvent()).isNotNull();
        assertThat(dispatchedNotification.getEvent().getSequenceId()).isEqualTo(eventId);
        assertThat(dispatchedNotification.getEvent().getType()).isEqualTo("DATA_PRODUCT_CREATED");
        assertThat(dispatchedNotification.getEvent().getResourceType()).isEqualTo("DATA_PRODUCT");
        assertThat(dispatchedNotification.getEvent().getResourceIdentifier()).isEqualTo("d5b5b9ac-6a73-4c73-b9ce-4bfc10a1dba0");

        // Verify the event in the replayed notification response matches
        assertThat(replayedNotification.getEvent()).isNotNull();
        assertThat(replayedNotification.getEvent().getSequenceId()).isEqualTo(eventId);
        assertThat(replayedNotification.getEvent().getType()).isEqualTo("DATA_PRODUCT_CREATED");
        assertThat(replayedNotification.getSubscription()).isNotNull();
        assertThat(replayedNotification.getSubscription().getUuid()).isEqualTo(subscriptionUuid);
        assertThat(replayedNotification.getSubscription().getName()).isEqualTo(observerName);

        // Check the status of the notification (it should be ok)
        // Get the updated notification to check its status
        ResponseEntity<NotificationRes> updatedNotificationResponse = rest.getForEntity(
                apiUrl(RoutesV2.NOTIFICATIONS, "/" + notificationSequenceId),
                NotificationRes.class
        );
        assertThat(updatedNotificationResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(updatedNotificationResponse.getBody()).isNotNull();
        NotificationRes updatedNotification = updatedNotificationResponse.getBody();
        assertThat(updatedNotification.getStatus()).isIn(NotificationStatusRes.PROCESSING, NotificationStatusRes.PROCESSED);

        // Cleanup
        rest.delete(apiUrl(RoutesV2.EVENTS, "/" + eventId));
        rest.delete(apiUrl(RoutesV2.SUBSCRIPTIONS, "/" + subscriptionUuid));
    }

    private JsonNode jsonNode(String json) {
        try {
            return objectMapper.readTree(json);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
