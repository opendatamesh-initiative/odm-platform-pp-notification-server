package org.opendatamesh.platform.pp.notification.rest.v1.controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.opendatamesh.platform.pp.notification.rest.NotificationApplicationIT;
import org.opendatamesh.platform.pp.notification.rest.RoutesV1;
import org.opendatamesh.platform.pp.notification.rest.RoutesV2;
import org.opendatamesh.platform.pp.notification.rest.v1.resources.EventResV1;
import org.opendatamesh.platform.pp.notification.utils.client.jackson.PageUtility;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

public class EventControllerV1IT extends NotificationApplicationIT {

    @Test
    public void whenGetEventByIdThenReturnEvent() {
        // Given - Create event using V1 dispatch API
        String entityId = "d5b5b9ac-6a73-4c73-b9ce-4bfc10a1dba0";
        String eventType = "DATA_PRODUCT_CREATED";
        EventResV1 event = new EventResV1();
        event.setType(eventType);
        event.setEntityId(entityId);
        event.setBeforeState(jsonNode("{\"test\": \"before\"}"));
        event.setAfterState(jsonNode("{\"test\": \"after\"}"));

        // Dispatch the event
        ResponseEntity<Void> dispatchResponse = rest.postForEntity(
                apiUrl(RoutesV1.DISPATCH),
                new HttpEntity<>(event),
                Void.class
        );
        assertThat(dispatchResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        // Search for the created event to get its ID
        ResponseEntity<PageUtility<EventResV1>> searchResponse = rest.exchange(
                apiUrl(RoutesV1.EVENTS) + "?eventType=" + eventType + "&entityId=" + entityId,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );
        assertThat(searchResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(searchResponse.getBody()).isNotNull();
        Page<EventResV1> eventsPage = searchResponse.getBody();
        assertThat(eventsPage).isNotNull();
        assertThat(eventsPage.getContent()).isNotEmpty();

        EventResV1 createdEvent = eventsPage.getContent().stream()
                .filter(e -> eventType.equals(e.getType()) && entityId.equals(e.getEntityId()))
                .findFirst()
                .orElse(null);
        assertThat(createdEvent).isNotNull();
        Long eventId = createdEvent.getId();

        // When
        ResponseEntity<EventResV1> response = rest.getForEntity(
                apiUrl(RoutesV1.EVENTS, "/" + eventId),
                EventResV1.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getId()).isEqualTo(eventId);


        // Cleanup - use V2 API only for deletion
        rest.delete(apiUrl(RoutesV2.EVENTS, "/" + eventId));
    }

    @Test
    public void whenGetEventWithNonExistentIdThenReturnNotFound() {
        // Given
        Long nonExistentId = 999999L;

        // When
        ResponseEntity<String> response = rest.getForEntity(
                apiUrl(RoutesV1.EVENTS, "/" + nonExistentId),
                String.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    public void whenSearchEventsThenReturnEventsList() {
        // Given - Create multiple events using V1 dispatch API
        String baseEntityId = "d5b5b9ac-6a73-4c73-b9ce-4bfc10a1dba0-" + System.currentTimeMillis();
        String eventType1 = "DATA_PRODUCT_CREATED";
        String eventType2 = "DATA_PRODUCT_UPDATED";

        // Create first event
        EventResV1 event1 = new EventResV1();
        event1.setType(eventType1);
        event1.setEntityId(baseEntityId + "-1");
        event1.setBeforeState(jsonNode("{\"test\": \"before1\"}"));
        event1.setAfterState(jsonNode("{\"test\": \"after1\"}"));

        ResponseEntity<Void> dispatchResponse1 = rest.postForEntity(
                apiUrl(RoutesV1.DISPATCH),
                new HttpEntity<>(event1),
                Void.class
        );
        assertThat(dispatchResponse1.getStatusCode()).isEqualTo(HttpStatus.OK);

        // Create second event
        EventResV1 event2 = new EventResV1();
        event2.setType(eventType2);
        event2.setEntityId(baseEntityId + "-2");
        event2.setBeforeState(jsonNode("{\"test\": \"before2\"}"));
        event2.setAfterState(jsonNode("{\"test\": \"after2\"}"));

        ResponseEntity<Void> dispatchResponse2 = rest.postForEntity(
                apiUrl(RoutesV1.DISPATCH),
                new HttpEntity<>(event2),
                Void.class
        );
        assertThat(dispatchResponse2.getStatusCode()).isEqualTo(HttpStatus.OK);

        // When - Search for all events (no filters)
        ResponseEntity<PageUtility<EventResV1>> response = rest.exchange(
                apiUrl(RoutesV1.EVENTS),
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();

        Page<EventResV1> eventsPage = response.getBody();
        assertThat(eventsPage).isNotNull();
        assertThat(eventsPage.getContent()).isNotEmpty();

        // Verify our created events are in the results
        boolean foundEvent1 = eventsPage.getContent().stream()
                .anyMatch(e -> eventType1.equals(e.getType()) && (baseEntityId + "-1").equals(e.getEntityId()));
        boolean foundEvent2 = eventsPage.getContent().stream()
                .anyMatch(e -> eventType2.equals(e.getType()) && (baseEntityId + "-2").equals(e.getEntityId()));

        assertThat(foundEvent1).isTrue();
        assertThat(foundEvent2).isTrue();

        // Verify pagination metadata
        assertThat(eventsPage.getTotalElements()).isGreaterThanOrEqualTo(2);
        assertThat(eventsPage.getNumber()).isGreaterThanOrEqualTo(0);
        assertThat(eventsPage.getSize()).isGreaterThan(0);

        // Cleanup - use V2 API only for deletion
        // Find and delete event1
        ResponseEntity<PageUtility<EventResV1>> searchResponse1 = rest.exchange(
                apiUrl(RoutesV1.EVENTS) + "?eventType=" + eventType1 + "&entityId=" + baseEntityId + "-1",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );
        assertThat(searchResponse1.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(searchResponse1.getBody()).isNotNull();
        assertThat(searchResponse1.getBody().getContent()).isNotEmpty();
        Long eventId1 = searchResponse1.getBody().getContent().get(0).getId();
        rest.delete(apiUrl(RoutesV2.EVENTS, "/" + eventId1));

        // Find and delete event2
        ResponseEntity<PageUtility<EventResV1>> searchResponse2 = rest.exchange(
                apiUrl(RoutesV1.EVENTS) + "?eventType=" + eventType2 + "&entityId=" + baseEntityId + "-2",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );
        assertThat(searchResponse2.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(searchResponse2.getBody()).isNotNull();
        assertThat(searchResponse2.getBody().getContent()).isNotEmpty();
        Long eventId2 = searchResponse2.getBody().getContent().get(0).getId();
        rest.delete(apiUrl(RoutesV2.EVENTS, "/" + eventId2));
    }

    @Test
    public void whenSearchEventsWithEventTypeThenReturnFilteredEvents() {
        // Given - Create event using V1 dispatch API
        String entityId = "d5b5b9ac-6a73-4c73-b9ce-4bfc10a1dba0-" + System.currentTimeMillis();
        String eventType = "DATA_PRODUCT_CREATED";
        EventResV1 event = new EventResV1();
        event.setType(eventType);
        event.setEntityId(entityId);
        event.setBeforeState(jsonNode("{\"test\": \"before\"}"));
        event.setAfterState(jsonNode("{\"test\": \"after\"}"));

        // Dispatch the event
        ResponseEntity<Void> dispatchResponse = rest.postForEntity(
                apiUrl(RoutesV1.DISPATCH),
                new HttpEntity<>(event),
                Void.class
        );
        assertThat(dispatchResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        // When - Search for events by type
        ResponseEntity<PageUtility<EventResV1>> response = rest.exchange(
                apiUrl(RoutesV1.EVENTS) + "?eventType=" + eventType,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();

        // Verify our event is in the results
        Page<EventResV1> eventsPage = response.getBody();
        assertThat(eventsPage).isNotNull();
        boolean foundOurEvent = eventsPage.getContent().stream()
                .anyMatch(e -> eventType.equals(e.getType()) && entityId.equals(e.getEntityId()));
        assertThat(foundOurEvent).isTrue();

        // Cleanup - use V2 API only for deletion
        ResponseEntity<PageUtility<EventResV1>> searchResponse = rest.exchange(
                apiUrl(RoutesV1.EVENTS) + "?eventType=" + eventType + "&entityId=" + entityId,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );

        Long eventId = searchResponse.getBody().getContent().get(0).getId();
        rest.delete(apiUrl(RoutesV2.EVENTS, "/" + eventId));

    }

    @Test
    public void whenSearchEventsWithEntityIdThenReturnFilteredEvents() {
        // Given - Create event using V1 dispatch API
        String entityId = "d5b5b9ac-6a73-4c73-b9ce-4bfc10a1dba0-" + System.currentTimeMillis();
        String eventType = "DATA_PRODUCT_CREATED";
        EventResV1 event = new EventResV1();
        event.setType(eventType);
        event.setEntityId(entityId);
        event.setBeforeState(jsonNode("{\"test\": \"before\"}"));
        event.setAfterState(jsonNode("{\"test\": \"after\"}"));

        // Dispatch the event
        ResponseEntity<Void> dispatchResponse = rest.postForEntity(
                apiUrl(RoutesV1.DISPATCH),
                new HttpEntity<>(event),
                Void.class
        );
        assertThat(dispatchResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        // When - Search for events by entityId
        ResponseEntity<PageUtility<EventResV1>> response = rest.exchange(
                apiUrl(RoutesV1.EVENTS) + "?entityId=" + entityId,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();

        // Verify our event is in the results
        Page<EventResV1> eventsPage = response.getBody();
        assertThat(eventsPage).isNotNull();
        boolean foundOurEvent = eventsPage.getContent().stream()
                .anyMatch(e -> eventType.equals(e.getType()) && entityId.equals(e.getEntityId()));
        assertThat(foundOurEvent).isTrue();

        // Cleanup - use V2 API only for deletion
        ResponseEntity<PageUtility<EventResV1>> searchResponse = rest.exchange(
                apiUrl(RoutesV1.EVENTS) + "?eventType=" + eventType + "&entityId=" + entityId,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );

        Long eventId = searchResponse.getBody().getContent().get(0).getId();
        rest.delete(apiUrl(RoutesV2.EVENTS, "/" + eventId));

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

