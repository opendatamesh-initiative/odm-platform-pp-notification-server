package org.opendatamesh.platform.pp.notification.rest.v2.controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.opendatamesh.platform.pp.notification.client.ObserverClient;
import org.opendatamesh.platform.pp.notification.notification.entities.Notification;
import org.opendatamesh.platform.pp.notification.notification.entities.NotificationStatus;
import org.opendatamesh.platform.pp.notification.rest.NotificationApplicationIT;
import org.opendatamesh.platform.pp.notification.rest.RoutesV2;
import org.opendatamesh.platform.pp.notification.rest.v2.resources.event.EventRes;
import org.opendatamesh.platform.pp.notification.rest.v2.resources.event.usecases.emit.EventEmitCommandRes;
import org.opendatamesh.platform.pp.notification.rest.v2.resources.event.usecases.emit.EventEmitResponseRes;
import org.opendatamesh.platform.pp.notification.rest.v2.resources.notification.NotificationRes;
import org.opendatamesh.platform.pp.notification.rest.v2.resources.notification.NotificationStatusRes;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class EventUseCaseControllerIT extends NotificationApplicationIT {

    @Autowired
    private ObserverClient observerClient;

    @BeforeEach
    public void setUp() {
        reset(observerClient);
    }

    @AfterEach
    public void tearDown() {
        reset(observerClient);
    }

    @Test
    public void whenEmitEventThenReturnCreatedEvent() {
        // Given
        EventEmitCommandRes.Event event = new EventEmitCommandRes.Event();
        event.setResourceType("DATA_PRODUCT");
        event.setResourceIdentifier("d5b5b9ac-6a73-4c73-b9ce-4bfc10a1dba0");
        event.setType("DATA_PRODUCT_CREATED");
        event.setEventTypeVersion("1.0.0");
        event.setEventContent(jsonNode("{\"test\": \"content\"}"));

        EventEmitCommandRes emitCommand = new EventEmitCommandRes(event);

        // When
        ResponseEntity<EventEmitResponseRes> response = rest.postForEntity(
                apiUrl(RoutesV2.EVENTS, "/emit"),
                new HttpEntity<>(emitCommand),
                EventEmitResponseRes.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getEvent()).isNotNull();
        EventEmitResponseRes.Event createdEvent = response.getBody().getEvent();
        assertThat(createdEvent.getSequenceId()).isNotNull();
        assertThat(createdEvent.getResourceType()).isEqualTo(event.getResourceType());
        assertThat(createdEvent.getResourceIdentifier()).isEqualTo(event.getResourceIdentifier());
        assertThat(createdEvent.getType()).isEqualTo(event.getType());
        assertThat(createdEvent.getEventTypeVersion()).isEqualTo(event.getEventTypeVersion());
        assertThat(createdEvent.getEventContent()).isNotNull();
        assertThat(createdEvent.getCreatedAt()).isNotNull();
        assertThat(createdEvent.getUpdatedAt()).isNotNull();

        // Cleanup
        rest.delete(apiUrl(RoutesV2.EVENTS, "/" + createdEvent.getSequenceId()));
    }

    @Test
    public void whenEmitEventWithSubscriptionThenObserverClientIsCalled() {
        // Given - Create subscription for the event type
        SubscribeCommandRes subscribeCommand = new SubscribeCommandRes();
        subscribeCommand.setObserverName("test-observer-emit");
        subscribeCommand.setObserverDisplayName("Test Observer for Emit");
        subscribeCommand.setObserverBaseUrl("https://observer.example.com/api/v1");
        subscribeCommand.setObserverApiVersion("v1");
        subscribeCommand.setEventTypes(Arrays.asList("DATA_PRODUCT_CREATED"));

        ResponseEntity<SubscribeResponseRes> subscriptionResponse = rest.postForEntity(
                apiUrl(RoutesV2.SUBSCRIPTIONS, "/subscribe"),
                new HttpEntity<>(subscribeCommand),
                SubscribeResponseRes.class
        );
        assertThat(subscriptionResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(subscriptionResponse.getBody()).isNotNull();
        assertThat(subscriptionResponse.getBody().getSubscription()).isNotNull();
        SubscribeResponseRes.Subscription subscription = subscriptionResponse.getBody().getSubscription();
        String subscriptionUuid = subscription.getUuid();

        // Create event to emit
        EventEmitCommandRes.Event event = new EventEmitCommandRes.Event();
        event.setResourceType("DATA_PRODUCT");
        event.setResourceIdentifier("d5b5b9ac-6a73-4c73-b9ce-4bfc10a1dba0");
        event.setType("DATA_PRODUCT_CREATED");
        event.setEventTypeVersion("1.0.0");
        event.setEventContent(jsonNode("{\"test\": \"content\"}"));

        EventEmitCommandRes emitCommand = new EventEmitCommandRes(event);

        // When
        ResponseEntity<EventEmitResponseRes> response = rest.postForEntity(
                apiUrl(RoutesV2.EVENTS, "/emit"),
                new HttpEntity<>(emitCommand),
                EventEmitResponseRes.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getEvent()).isNotNull();
        EventEmitResponseRes.Event createdEvent = response.getBody().getEvent();
        assertThat(createdEvent.getSequenceId()).isNotNull();

        // Verify ObserverClient was called with correct Notification
        ArgumentCaptor<Notification> notificationCaptor =
                ArgumentCaptor.forClass(Notification.class);
        verify(observerClient).dispatchNotification(notificationCaptor.capture());

        Notification dispatchedNotification = notificationCaptor.getValue();
        assertThat(dispatchedNotification).isNotNull();
        assertThat(dispatchedNotification.getStatus()).isEqualTo(NotificationStatus.PROCESSING);
        assertThat(dispatchedNotification.getEvent()).isNotNull();
        assertThat(dispatchedNotification.getEvent().getSequenceId()).isEqualTo(createdEvent.getSequenceId());
        assertThat(dispatchedNotification.getEvent().getType()).isEqualTo("DATA_PRODUCT_CREATED");
        assertThat(dispatchedNotification.getSubscription()).isNotNull();
        assertThat(dispatchedNotification.getSubscription().getUuid()).isEqualTo(subscriptionUuid);
        assertThat(dispatchedNotification.getSubscription().getName()).isEqualTo(subscribeCommand.getObserverName());

        // Cleanup
        rest.delete(apiUrl(RoutesV2.EVENTS, "/" + createdEvent.getSequenceId()));
        rest.delete(apiUrl(RoutesV2.SUBSCRIPTIONS, "/" + subscriptionUuid));
    }

    @Test
    public void whenEmitEventWithMissingRequiredFieldThenReturnBadRequest() {
        // Given
        EventEmitCommandRes.Event event = new EventEmitCommandRes.Event();
        event.setResourceType("DATA_PRODUCT");
        // Missing resourceIdentifier, type, eventTypeVersion, and eventContent

        EventEmitCommandRes emitCommand = new EventEmitCommandRes(event);

        // When
        ResponseEntity<String> response = rest.postForEntity(
                apiUrl(RoutesV2.EVENTS, "/emit"),
                new HttpEntity<>(emitCommand),
                String.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void whenEmitEventWithEmptyEventContentThenReturnCreatedEvent() {
        // Given
        EventEmitCommandRes.Event event = new EventEmitCommandRes.Event();
        event.setResourceType("DATA_PRODUCT");
        event.setResourceIdentifier("d5b5b9ac-6a73-4c73-b9ce-4bfc10a1dba0");
        event.setType("DATA_PRODUCT_CREATED");
        event.setEventTypeVersion("1.0.0");
        event.setEventContent(jsonNode("{}"));

        EventEmitCommandRes emitCommand = new EventEmitCommandRes(event);

        // When
        ResponseEntity<EventEmitResponseRes> response = rest.postForEntity(
                apiUrl(RoutesV2.EVENTS, "/emit"),
                new HttpEntity<>(emitCommand),
                EventEmitResponseRes.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getEvent()).isNotNull();
        EventEmitResponseRes.Event createdEvent = response.getBody().getEvent();
        assertThat(createdEvent.getSequenceId()).isNotNull();

        // Cleanup
        rest.delete(apiUrl(RoutesV2.EVENTS, "/" + createdEvent.getSequenceId()));
    }

    @Test
    public void whenObserverThrowsExceptionThenEventIsCreatedAndOtherObserversAreStillCalled() {
        // Given - Create two subscriptions for the event type
        SubscribeCommandRes subscribeCommand1 = new SubscribeCommandRes();
        subscribeCommand1.setObserverName("test-observer-failing");
        subscribeCommand1.setObserverDisplayName("Test Observer Failing");
        subscribeCommand1.setObserverBaseUrl("https://observer-failing.example.com/api/v1");
        subscribeCommand1.setObserverApiVersion("v1");
        subscribeCommand1.setEventTypes(Arrays.asList("DATA_PRODUCT_CREATED"));

        ResponseEntity<SubscribeResponseRes> subscriptionResponse1 = rest.postForEntity(
                apiUrl(RoutesV2.SUBSCRIPTIONS, "/subscribe"),
                new HttpEntity<>(subscribeCommand1),
                SubscribeResponseRes.class
        );
        assertThat(subscriptionResponse1.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(subscriptionResponse1.getBody()).isNotNull();
        assertThat(subscriptionResponse1.getBody().getSubscription()).isNotNull();
        String subscriptionUuid1 = subscriptionResponse1.getBody().getSubscription().getUuid();

        SubscribeCommandRes subscribeCommand2 = new SubscribeCommandRes();
        subscribeCommand2.setObserverName("test-observer-success");
        subscribeCommand2.setObserverDisplayName("Test Observer Success");
        subscribeCommand2.setObserverBaseUrl("https://observer-success.example.com/api/v1");
        subscribeCommand2.setObserverApiVersion("v1");
        subscribeCommand2.setEventTypes(Arrays.asList("DATA_PRODUCT_CREATED"));

        ResponseEntity<SubscribeResponseRes> subscriptionResponse2 = rest.postForEntity(
                apiUrl(RoutesV2.SUBSCRIPTIONS, "/subscribe"),
                new HttpEntity<>(subscribeCommand2),
                SubscribeResponseRes.class
        );
        assertThat(subscriptionResponse2.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(subscriptionResponse2.getBody()).isNotNull();
        assertThat(subscriptionResponse2.getBody().getSubscription()).isNotNull();
        String subscriptionUuid2 = subscriptionResponse2.getBody().getSubscription().getUuid();

        // Configure mock to throw exception only for the first subscription
        doAnswer(invocation -> {
            Notification notification = invocation.getArgument(0);
            if (notification.getSubscription().getUuid().equals(subscriptionUuid1)) {
                throw new RuntimeException("THIS IS A TEST THROWN EXCEPTION!!! Observer connection failed");
            }
            return null;
        }).when(observerClient).dispatchNotification(any(Notification.class));

        // Create event to emit
        EventEmitCommandRes.Event event = new EventEmitCommandRes.Event();
        event.setResourceType("DATA_PRODUCT");
        event.setResourceIdentifier("d5b5b9ac-6a73-4c73-b9ce-4bfc10a1dba0");
        event.setType("DATA_PRODUCT_CREATED");
        event.setEventTypeVersion("1.0.0");
        event.setEventContent(jsonNode("{\"test\": \"content\"}"));

        EventEmitCommandRes emitCommand = new EventEmitCommandRes(event);

        // When
        ResponseEntity<EventEmitResponseRes> response = rest.postForEntity(
                apiUrl(RoutesV2.EVENTS, "/emit"),
                new HttpEntity<>(emitCommand),
                EventEmitResponseRes.class
        );

        // Then - Verify event was created and returned
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getEvent()).isNotNull();
        EventEmitResponseRes.Event createdEvent = response.getBody().getEvent();
        Long eventId = createdEvent.getSequenceId();
        assertThat(eventId).isNotNull();

        // Verify event is persisted by getting it again
        ResponseEntity<EventRes> getEventResponse = rest.getForEntity(
                apiUrl(RoutesV2.EVENTS, "/" + eventId),
                EventRes.class
        );
        assertThat(getEventResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(getEventResponse.getBody()).isNotNull();
        assertThat(getEventResponse.getBody().getSequenceId()).isEqualTo(eventId);
        assertThat(getEventResponse.getBody().getType()).isEqualTo("DATA_PRODUCT_CREATED");

        // Verify ObserverClient was called for both subscriptions
        ArgumentCaptor<Notification> notificationCaptor = ArgumentCaptor.forClass(Notification.class);
        verify(observerClient, times(2)).dispatchNotification(notificationCaptor.capture());

        // Verify all notifications were created with correct statuses
        ResponseEntity<PageUtility<NotificationRes>> notifications1Response = rest.exchange(
                apiUrl(RoutesV2.NOTIFICATIONS) + "?subscriptionUuid=" + subscriptionUuid1,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );
        assertThat(notifications1Response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(notifications1Response.getBody()).isNotNull();
        Page<NotificationRes> notifications1 = notifications1Response.getBody();
        assertThat(notifications1.getContent()).hasSize(1);
        NotificationRes notification1 = notifications1.getContent().get(0);
        assertThat(notification1.getStatus()).isEqualTo(NotificationStatusRes.FAILED_TO_DELIVER);
        assertThat(notification1.getEvent()).isNotNull();
        assertThat(notification1.getEvent().getSequenceId()).isEqualTo(eventId);
        assertThat(notification1.getErrorMessage()).isNotNull();

        ResponseEntity<PageUtility<NotificationRes>> notifications2Response = rest.exchange(
                apiUrl(RoutesV2.NOTIFICATIONS) + "?subscriptionUuid=" + subscriptionUuid2,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );
        assertThat(notifications2Response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(notifications2Response.getBody()).isNotNull();
        Page<NotificationRes> notifications2 = notifications2Response.getBody();
        assertThat(notifications2.getContent()).hasSize(1);
        NotificationRes notification2 = notifications2.getContent().get(0);
        assertThat(notification2.getStatus()).isEqualTo(NotificationStatusRes.PROCESSING);
        assertThat(notification2.getEvent()).isNotNull();
        assertThat(notification2.getEvent().getSequenceId()).isEqualTo(eventId);
        assertThat(notification2.getErrorMessage()).isNull();

        // Cleanup
        rest.delete(apiUrl(RoutesV2.EVENTS, "/" + eventId));
        rest.delete(apiUrl(RoutesV2.SUBSCRIPTIONS, "/" + subscriptionUuid1));
        rest.delete(apiUrl(RoutesV2.SUBSCRIPTIONS, "/" + subscriptionUuid2));
    }

    private final ObjectMapper objectMapper = new ObjectMapper();

    private JsonNode jsonNode(String json) {
        try {
            return objectMapper.readTree(json);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

