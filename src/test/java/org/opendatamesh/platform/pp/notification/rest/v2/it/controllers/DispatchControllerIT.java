package org.opendatamesh.platform.pp.notification.rest.v2.it.controllers;

import org.junit.jupiter.api.Test;
import org.opendatamesh.platform.pp.notification.rest.v2.it.NotificationApplicationIT;
import org.opendatamesh.platform.pp.notification.rest.v2.it.RoutesV2;
import org.opendatamesh.platform.pp.notification.rest.v2.resources.event.EventRes;
import org.opendatamesh.platform.pp.notification.rest.v2.resources.event.EventType;
import org.opendatamesh.platform.pp.notification.rest.v2.resources.subscription.SubscriptionRes;
import org.opendatamesh.platform.pp.notification.rest.v2.resources.subscription.SubscriptionEventTypeRes;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Dispatch Controller Integration Tests
 * Replicating old DispatchIT.java for backward compatibility
 */
public class DispatchControllerIT extends NotificationApplicationIT {

    @Test
    public void testDispatchEvent() {
        // Resources + Creation
        SubscriptionRes subscription = new SubscriptionRes();
        subscription.setName("test-observer-" + System.currentTimeMillis());
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

        EventRes eventToDispatch = new EventRes();
        eventToDispatch.setResourceType("DATA_PRODUCT");
        eventToDispatch.setResourceIdentifier("dp-123");
        eventToDispatch.setType(EventType.DATAPRODUCT_CREATED);
        eventToDispatch.setEventTypeVersion("1.0.0");
        eventToDispatch.setEventContent("{\"id\": \"dp-123\", \"name\": \"Test Data Product\"}");

        // POST request
        ResponseEntity<Void> dispatchResponse = rest.postForEntity(
                apiUrl(RoutesV2.DISPATCH),
                new HttpEntity<>(eventToDispatch),
                Void.class
        );
        assertThat(dispatchResponse.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED);

        // Check event
        ResponseEntity<String> getResponse = rest.getForEntity(
                apiUrl(RoutesV2.EVENTS),
                String.class
        );
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(getResponse.getBody()).isNotNull();
        assertThat(getResponse.getBody()).contains("DATAPRODUCT_CREATED");
        assertThat(getResponse.getBody()).contains("dp-123");

        // Check notification
        getResponse = rest.getForEntity(
                apiUrl(RoutesV2.NOTIFICATIONS),
                String.class
        );
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(getResponse.getBody()).isNotNull();
        assertThat(getResponse.getBody()).contains("DATAPRODUCT_CREATED");

        // Cleanup
        rest.delete(apiUrl(RoutesV2.SUBSCRIPTIONS, "/" + createSubResponse.getBody().getUuid()));
    }
}
