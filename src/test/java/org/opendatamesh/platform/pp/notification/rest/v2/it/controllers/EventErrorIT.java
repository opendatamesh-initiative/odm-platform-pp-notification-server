package org.opendatamesh.platform.pp.notification.rest.v2.it.controllers;

import org.junit.jupiter.api.Test;
import org.opendatamesh.platform.pp.notification.rest.v2.it.NotificationApplicationIT;
import org.opendatamesh.platform.pp.notification.rest.v2.it.RoutesV2;
import org.opendatamesh.platform.pp.notification.rest.v2.resources.event.EventRes;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Error handling tests for Event operations
 */
public class EventErrorIT extends NotificationApplicationIT {

    @Test
    public void testEmitEventWithInvalidData_ReturnsBadRequest() {
        // Given - Missing required fields
        EventRes invalidEvent = new EventRes();
        invalidEvent.setResourceType("DATA_PRODUCT");

        // When
        ResponseEntity<String> response = rest.postForEntity(
                apiUrl(RoutesV2.DISPATCH),
                new HttpEntity<>(invalidEvent),
                String.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void testEmitNullEvent_ReturnsBadRequest() {
        // Given - Null event
        EventRes nullEvent = null;

        // When
        ResponseEntity<String> response = rest.postForEntity(
                apiUrl(RoutesV2.DISPATCH),
                new HttpEntity<>(nullEvent),
                String.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void testReadNonExistentEvent_ReturnsNotFound() {
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
}

