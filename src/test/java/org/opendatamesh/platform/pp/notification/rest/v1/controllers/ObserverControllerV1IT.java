package org.opendatamesh.platform.pp.notification.rest.v1.controllers;

import org.junit.jupiter.api.Test;
import org.opendatamesh.platform.pp.notification.rest.NotificationApplicationIT;
import org.opendatamesh.platform.pp.notification.rest.RoutesV1;
import org.opendatamesh.platform.pp.notification.rest.v1.resources.ObserverResV1;
import org.opendatamesh.platform.pp.notification.utils.client.jackson.PageUtility;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

public class ObserverControllerV1IT extends NotificationApplicationIT {

    @Test
    public void whenCreateObserverThenReturnCreatedObserver() {
        // Given
        ObserverResV1 observer = new ObserverResV1();
        observer.setName("test-observer");
        observer.setDisplayName("Test Observer");
        observer.setObserverServerBaseUrl("https://observer.example.com/api/v1");

        // When
        ResponseEntity<ObserverResV1> response = rest.postForEntity(
                apiUrl(RoutesV1.OBSERVERS),
                new HttpEntity<>(observer),
                ObserverResV1.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        ObserverResV1 createdObserver = response.getBody();

        assertThat(createdObserver.getId()).isNotNull();
        assertThat(createdObserver.getName()).isEqualTo(observer.getName());
        assertThat(createdObserver.getDisplayName()).isEqualTo(observer.getDisplayName());
        assertThat(createdObserver.getObserverServerBaseUrl()).isEqualTo(observer.getObserverServerBaseUrl());

        // Cleanup
        rest.delete(apiUrl(RoutesV1.OBSERVERS, "/" + createdObserver.getId()));

    }

    @Test
    public void whenGetObserverByIdThenReturnObserver() {
        // Given - Create observer first
        ObserverResV1 observer = new ObserverResV1();
        observer.setName("test-observer");
        observer.setDisplayName("Test Observer");
        observer.setObserverServerBaseUrl("https://observer.example.com/api/v1");

        ResponseEntity<ObserverResV1> createResponse = rest.postForEntity(
                apiUrl(RoutesV1.OBSERVERS),
                new HttpEntity<>(observer),
                ObserverResV1.class
        );
        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(createResponse.getBody()).isNotNull();
        Long observerId = createResponse.getBody().getId();
        assertThat(observerId).isNotNull();

        // When
        ResponseEntity<ObserverResV1> response = rest.getForEntity(
                apiUrl(RoutesV1.OBSERVERS, "/" + observerId),
                ObserverResV1.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getId()).isEqualTo(observerId);
        assertThat(response.getBody().getName()).isEqualTo(observer.getName());


        // Cleanup
        rest.delete(apiUrl(RoutesV1.OBSERVERS, "/" + observerId));
    }

    @Test
    public void whenGetObserverWithNonExistentIdThenReturnNotFound() {
        // Given
        Long nonExistentId = 999999L;

        // When
        ResponseEntity<String> response = rest.getForEntity(
                apiUrl(RoutesV1.OBSERVERS, "/" + nonExistentId),
                String.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    public void whenUpdateObserverThenReturnUpdatedObserver() {
        // Given - Create observer first
        ObserverResV1 initialObserver = new ObserverResV1();
        initialObserver.setName("test-observer");
        initialObserver.setDisplayName("Initial Display Name");
        initialObserver.setObserverServerBaseUrl("https://observer.example.com/api/v1");

        ResponseEntity<ObserverResV1> createResponse = rest.postForEntity(
                apiUrl(RoutesV1.OBSERVERS),
                new HttpEntity<>(initialObserver),
                ObserverResV1.class
        );
        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(createResponse.getBody()).isNotNull();
        Long observerId = createResponse.getBody().getId();
        assertThat(observerId).isNotNull();

        // Create updated observer
        ObserverResV1 updatedObserver = new ObserverResV1();
        updatedObserver.setName("test-observer");
        updatedObserver.setDisplayName("Updated Display Name");
        updatedObserver.setObserverServerBaseUrl("https://observer-updated.example.com/api/v1");

        // When
        ResponseEntity<ObserverResV1> response = rest.exchange(
                apiUrl(RoutesV1.OBSERVERS, "/" + observerId),
                HttpMethod.PUT,
                new HttpEntity<>(updatedObserver),
                ObserverResV1.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();

        assertThat(response.getBody().getId()).isEqualTo(observerId);
        assertThat(response.getBody().getDisplayName()).isEqualTo("Updated Display Name");
        assertThat(response.getBody().getObserverServerBaseUrl()).isEqualTo("https://observer-updated.example.com/api/v1");

        // Cleanup
        rest.delete(apiUrl(RoutesV1.OBSERVERS, "/" + observerId));
    }

    @Test
    public void whenUpdateObserverWithNonExistentIdThenReturnNotFound() {
        // Given
        Long nonExistentId = 999999L;
        ObserverResV1 observer = new ObserverResV1();
        observer.setName("test-observer");
        observer.setDisplayName("Test Observer");
        observer.setObserverServerBaseUrl("https://observer.example.com/api/v1");

        // When
        ResponseEntity<String> response = rest.exchange(
                apiUrl(RoutesV1.OBSERVERS, "/" + nonExistentId),
                HttpMethod.PUT,
                new HttpEntity<>(observer),
                String.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    public void whenSearchObserversThenReturnObserversList() {
        // Given - Create first observer
        ObserverResV1 observer1 = new ObserverResV1();
        observer1.setName("test-observer-1");
        observer1.setDisplayName("Test Observer 1");
        observer1.setObserverServerBaseUrl("https://observer1.example.com/api/v1");

        ResponseEntity<ObserverResV1> createResponse1 = rest.postForEntity(
                apiUrl(RoutesV1.OBSERVERS),
                new HttpEntity<>(observer1),
                ObserverResV1.class
        );
        assertThat(createResponse1.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(createResponse1.getBody()).isNotNull();
        Long observer1Id = createResponse1.getBody().getId();
        assertThat(observer1Id).isNotNull();

        // Create second observer
        ObserverResV1 observer2 = new ObserverResV1();
        observer2.setName("test-observer-2");
        observer2.setDisplayName("Test Observer 2");
        observer2.setObserverServerBaseUrl("https://observer2.example.com/api/v1");

        ResponseEntity<ObserverResV1> createResponse2 = rest.postForEntity(
                apiUrl(RoutesV1.OBSERVERS),
                new HttpEntity<>(observer2),
                ObserverResV1.class
        );
        assertThat(createResponse2.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(createResponse2.getBody()).isNotNull();
        Long observer2Id = createResponse2.getBody().getId();
        assertThat(observer2Id).isNotNull();

        // When
        ResponseEntity<PageUtility<ObserverResV1>> response = rest.exchange(
                apiUrl(RoutesV1.OBSERVERS),
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();

        // Cleanup

        rest.delete(apiUrl(RoutesV1.OBSERVERS, "/" + observer1Id));
        rest.delete(apiUrl(RoutesV1.OBSERVERS, "/" + observer2Id));
    }

    @Test
    public void whenDeleteObserverThenReturnOkAndObserverIsDeleted() {
        // Given - Create observer first
        ObserverResV1 observer = new ObserverResV1();
        observer.setName("test-observer");
        observer.setDisplayName("Test Observer");
        observer.setObserverServerBaseUrl("https://observer.example.com/api/v1");

        ResponseEntity<ObserverResV1> createResponse = rest.postForEntity(
                apiUrl(RoutesV1.OBSERVERS),
                new HttpEntity<>(observer),
                ObserverResV1.class
        );
        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(createResponse.getBody()).isNotNull();
        Long observerId = createResponse.getBody().getId();
        assertThat(observerId).isNotNull();

        // When
        ResponseEntity<Void> response = rest.exchange(
                apiUrl(RoutesV1.OBSERVERS, "/" + observerId),
                HttpMethod.DELETE,
                null,
                Void.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        // Verify deletion
        ResponseEntity<String> getResponse = rest.getForEntity(
                apiUrl(RoutesV1.OBSERVERS, "/" + observerId),
                String.class
        );
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    public void whenDeleteObserverWithNonExistentIdThenReturnNotFound() {
        // Given
        Long nonExistentId = 999999L;

        // When
        ResponseEntity<String> response = rest.exchange(
                apiUrl(RoutesV1.OBSERVERS, "/" + nonExistentId),
                HttpMethod.DELETE,
                null,
                String.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }
}

