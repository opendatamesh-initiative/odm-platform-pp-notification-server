package org.opendatamesh.platform.pp.notification.rest.v1.controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.opendatamesh.platform.pp.notification.client.ObserverClient;
import org.opendatamesh.platform.pp.notification.rest.NotificationApplicationIT;
import org.opendatamesh.platform.pp.notification.rest.RoutesV1;
import org.opendatamesh.platform.pp.notification.rest.RoutesV2;
import org.opendatamesh.platform.pp.notification.rest.v1.resources.EventNotificationResV1;
import org.opendatamesh.platform.pp.notification.rest.v1.resources.EventResV1;
import org.opendatamesh.platform.pp.notification.rest.v1.resources.ObserverResV1;
import org.opendatamesh.platform.pp.notification.rest.v2.resources.subscription.SubscriptionRes;
import org.opendatamesh.platform.pp.notification.utils.client.jackson.PageUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.reset;

public class DispatchControllerV1IT extends NotificationApplicationIT {

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

    private JsonNode jsonNode(String json) {
        try {
            return objectMapper.readTree(json);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void whenDispatchEventThenReturnOk() {
        // Given - Create observer (equivalent to subscription in V1)
        ObserverResV1 observer = new ObserverResV1();
        observer.setName("test-observer-dispatch");
        observer.setDisplayName("Test Observer for Dispatch");
        observer.setObserverServerBaseUrl("https://observer.example.com/api/v1");

        ResponseEntity<ObserverResV1> observerResponse = rest.postForEntity(
                apiUrl(RoutesV1.OBSERVERS),
                new HttpEntity<>(observer),
                ObserverResV1.class
        );
        assertThat(observerResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(observerResponse.getBody()).isNotNull();
        ObserverResV1 createdObserver = observerResponse.getBody();
        assertThat(createdObserver).isNotNull();
        assertThat(createdObserver.getName()).isEqualTo(observer.getName());
        
        // Get subscription UUID for cleanup (using V2 search by name)
        ResponseEntity<PageUtility<SubscriptionRes>> subscriptionSearchResponse = rest.exchange(
                apiUrl(RoutesV2.SUBSCRIPTIONS) + "?name=" + observer.getName(),
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );
        assertThat(subscriptionSearchResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(subscriptionSearchResponse.getBody()).isNotNull();
        Page<SubscriptionRes> subscriptions = subscriptionSearchResponse.getBody();
        assertThat(subscriptions).isNotNull();
        assertThat(subscriptions.getContent()).isNotEmpty();
        String subscriptionUuid = subscriptions.getContent().get(0).getUuid();

        EventResV1 event = new EventResV1();
        event.setType("DATA_PRODUCT_CREATED");
        event.setEntityId("d5b5b9ac-6a73-4c73-b9ce-4bfc10a1dba0");
        event.setBeforeState(jsonNode("{\"test\": \"before\"}"));
        event.setAfterState(jsonNode("{\"test\": \"after\"}"));

        // When
        ResponseEntity<Void> response = rest.postForEntity(
                apiUrl(RoutesV1.DISPATCH),
                new HttpEntity<>(event),
                Void.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        // Verify notifications were created
        ResponseEntity<PageUtility<EventNotificationResV1>> notificationsResponse = rest.exchange(
                apiUrl(RoutesV1.NOTIFICATIONS) + "?eventType=DATA_PRODUCT_CREATED",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );
        assertThat(notificationsResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(notificationsResponse.getBody()).isNotNull();
        Page<EventNotificationResV1> notifications = notificationsResponse.getBody();
        assertThat(notifications).isNotNull();
        assertThat(notifications.getContent()).isNotEmpty();
        
        // Verify at least one notification matches our event
        EventNotificationResV1 notification = notifications.getContent().stream()
                .filter(n -> n.getEvent() != null && 
                             "DATA_PRODUCT_CREATED".equals(n.getEvent().getType()) &&
                             "d5b5b9ac-6a73-4c73-b9ce-4bfc10a1dba0".equals(n.getEvent().getEntityId()))
                .findFirst()
                .orElse(null);
        assertThat(notification).isNotNull();
        assertThat(notification.getEvent()).isNotNull();
        assertThat(notification.getEvent().getType()).isEqualTo("DATA_PRODUCT_CREATED");
        assertThat(notification.getEvent().getEntityId()).isEqualTo("d5b5b9ac-6a73-4c73-b9ce-4bfc10a1dba0");
        assertThat(notification.getStatus()).isNotNull();

        // Cleanup
        rest.delete(apiUrl(RoutesV2.SUBSCRIPTIONS, "/" + subscriptionUuid));
    }
}

