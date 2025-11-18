package org.opendatamesh.platform.pp.notification.rest.v2.controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.opendatamesh.platform.pp.notification.rest.NotificationApplicationIT;
import org.opendatamesh.platform.pp.notification.rest.RoutesV2;
import org.opendatamesh.platform.pp.notification.rest.v2.resources.event.EventRes;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

public class EventControllerIT extends NotificationApplicationIT {

    private final ObjectMapper objectMapper = new ObjectMapper();

    private JsonNode jsonNode(String json) {
        try {
            return objectMapper.readTree(json);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void whenCreateEventThenReturnCreatedEvent() {
        // Given
        EventRes event = new EventRes();
        event.setResourceType("DATA_PRODUCT");
        event.setResourceIdentifier("d5b5b9ac-6a73-4c73-b9ce-4bfc10a1dba0");
        event.setType("DATA_PRODUCT_CREATED");
        event.setEventTypeVersion("1.0.0");
        event.setEventContent(jsonNode("{\"test\": \"content\"}"));

        // When
        ResponseEntity<EventRes> response = rest.postForEntity(
                apiUrl(RoutesV2.EVENTS),
                new HttpEntity<>(event),
                EventRes.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getSequenceId()).isNotNull();
        assertThat(response.getBody().getResourceType()).isEqualTo(event.getResourceType());
        assertThat(response.getBody().getType()).isEqualTo(event.getType());

        // Cleanup
        rest.delete(apiUrlOfItem(RoutesV2.EVENTS), response.getBody().getSequenceId());
    }

    @Test
    public void whenGetEventByIdThenReturnEvent() {
        // Given - Create and save event
        EventRes event = new EventRes();
        event.setResourceType("DATA_PRODUCT");
        event.setResourceIdentifier("d5b5b9ac-6a73-4c73-b9ce-4bfc10a1dba0");
        event.setType("DATA_PRODUCT_CREATED");
        event.setEventTypeVersion("1.0.0");
        event.setEventContent(jsonNode("{\"test\": \"content\"}"));

        ResponseEntity<EventRes> createResponse = rest.postForEntity(
                apiUrl(RoutesV2.EVENTS),
                new HttpEntity<>(event),
                EventRes.class
        );
        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        Long eventId = createResponse.getBody().getSequenceId();

        // When
        ResponseEntity<EventRes> response = rest.getForEntity(
                apiUrl(RoutesV2.EVENTS, "/" + eventId),
                EventRes.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getSequenceId()).isEqualTo(eventId);
        assertThat(response.getBody().getResourceType()).isEqualTo(event.getResourceType());

        // Cleanup
        rest.delete(apiUrlOfItem(RoutesV2.EVENTS), eventId);
    }

    @Test
    public void whenGetEventWithNonExistentIdThenReturnNotFound() {
        // Given
        Long nonExistentId = 999999L;

        // When
        ResponseEntity<String> response = rest.getForEntity(
                apiUrl(RoutesV2.EVENTS, "/" + nonExistentId),
                String.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    public void whenSearchEventsThenReturnEventsList() {
        // Given - Create and save first event
        EventRes event1 = new EventRes();
        event1.setResourceType("DATA_PRODUCT");
        event1.setResourceIdentifier("d5b5b9ac-6a73-4c73-b9ce-4bfc10a1dba0");
        event1.setType("DATA_PRODUCT_CREATED");
        event1.setEventTypeVersion("1.0.0");
        event1.setEventContent(jsonNode("{\"test\": \"content1\"}"));

        ResponseEntity<EventRes> createResponse1 = rest.postForEntity(
                apiUrl(RoutesV2.EVENTS),
                new HttpEntity<>(event1),
                EventRes.class
        );
        assertThat(createResponse1.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        Long event1Id = createResponse1.getBody().getSequenceId();

        // Create and save second event
        EventRes event2 = new EventRes();
        event2.setResourceType("DATA_PRODUCT");
        event2.setResourceIdentifier("e6c6c0bd-7b84-5d84-c0df-5cgd21b2ecb1");
        event2.setType("DATA_PRODUCT_UPDATED");
        event2.setEventTypeVersion("1.0.0");
        event2.setEventContent(jsonNode("{\"test\": \"content2\"}"));

        ResponseEntity<EventRes> createResponse2 = rest.postForEntity(
                apiUrl(RoutesV2.EVENTS),
                new HttpEntity<>(event2),
                EventRes.class
        );
        assertThat(createResponse2.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        Long event2Id = createResponse2.getBody().getSequenceId();

        // When
        ResponseEntity<String> response = rest.getForEntity(
                apiUrl(RoutesV2.EVENTS),
                String.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();

        // Cleanup
        rest.delete(apiUrlOfItem(RoutesV2.EVENTS), event1Id);
        rest.delete(apiUrlOfItem(RoutesV2.EVENTS), event2Id);
    }

    @Test
    public void whenUpdateEventThenReturnUpdatedEvent() {
        // Given - Create and save initial event
        EventRes initialEvent = new EventRes();
        initialEvent.setResourceType("DATA_PRODUCT");
        initialEvent.setResourceIdentifier("d5b5b9ac-6a73-4c73-b9ce-4bfc10a1dba0");
        initialEvent.setType("DATA_PRODUCT_CREATED");
        initialEvent.setEventTypeVersion("1.0.0");
        initialEvent.setEventContent(jsonNode("{\"test\": \"initial content\"}"));

        ResponseEntity<EventRes> createResponse = rest.postForEntity(
                apiUrl(RoutesV2.EVENTS),
                new HttpEntity<>(initialEvent),
                EventRes.class
        );
        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        Long eventId = createResponse.getBody().getSequenceId();

        // Create updated event
        EventRes updatedEvent = new EventRes();
        updatedEvent.setResourceType("DATA_PRODUCT");
        updatedEvent.setResourceIdentifier("d5b5b9ac-6a73-4c73-b9ce-4bfc10a1dba0");
        updatedEvent.setType("DATA_PRODUCT_UPDATED");
        updatedEvent.setEventTypeVersion("1.0.0");
        updatedEvent.setEventContent(jsonNode("{\"test\": \"updated content\"}"));

        // When
        ResponseEntity<EventRes> response = rest.exchange(
                apiUrl(RoutesV2.EVENTS, "/" + eventId),
                HttpMethod.PUT,
                new HttpEntity<>(updatedEvent),
                EventRes.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getSequenceId()).isEqualTo(eventId);
        assertThat(response.getBody().getType()).isEqualTo("DATA_PRODUCT_UPDATED");
        assertThat(response.getBody().getEventContent()).isEqualTo(jsonNode("{\"test\": \"updated content\"}"));

        // Cleanup
        rest.delete(apiUrlOfItem(RoutesV2.EVENTS), eventId);
    }

    @Test
    public void whenDeleteEventThenReturnNoContentAndEventIsDeleted() {
        // Given - Create and save event
        EventRes event = new EventRes();
        event.setResourceType("DATA_PRODUCT");
        event.setResourceIdentifier("d5b5b9ac-6a73-4c73-b9ce-4bfc10a1dba0");
        event.setType("DATA_PRODUCT_CREATED");
        event.setEventTypeVersion("1.0.0");
        event.setEventContent(jsonNode("{\"test\": \"content\"}"));

        ResponseEntity<EventRes> createResponse = rest.postForEntity(
                apiUrl(RoutesV2.EVENTS),
                new HttpEntity<>(event),
                EventRes.class
        );
        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        Long eventId = createResponse.getBody().getSequenceId();

        // When
        rest.delete(apiUrlOfItem(RoutesV2.EVENTS), eventId);


        // Verify deletion
        ResponseEntity<String> getResponse = rest.getForEntity(
                apiUrl(RoutesV2.EVENTS, "/" + eventId),
                String.class
        );
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }
}

