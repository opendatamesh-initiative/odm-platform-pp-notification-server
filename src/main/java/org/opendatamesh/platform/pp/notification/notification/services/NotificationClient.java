package org.opendatamesh.platform.pp.notification.notification.services;

import org.opendatamesh.platform.pp.notification.rest.v2.resources.notification.NotificationRes;
import org.opendatamesh.platform.pp.notification.rest.v2.resources.subscription.SubscriptionRes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.CompletableFuture;

@Service
public class NotificationClient {

    private static final Logger log = LoggerFactory.getLogger(NotificationClient.class);
    private final RestTemplate restTemplate;

    public NotificationClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public CompletableFuture<Void> dispatchNotificationToSubscription(
            SubscriptionRes subscription,
           NotificationRes notification) {

        return CompletableFuture.runAsync(() -> {
            if (subscription == null || subscription.getObserverServerBaseUrl() == null) {
                log.error("Cannot dispatch notification: subscription or base URL is null");
                return;
            }

            String url = subscription.getObserverServerBaseUrl() + "/notifications";

            try {
                log.info("Sending notification {} to subscription: {} at URL: {}",
                        notification.getSequenceId(), subscription.getUuid(), url);

                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);

                HttpEntity<Object> request = new HttpEntity<>(notification, headers);

                ResponseEntity<String> response = restTemplate.exchange(
                        url,
                        HttpMethod.POST,
                        request,
                        String.class
                );

                if (response.getStatusCode().is2xxSuccessful()) {
                    log.info("Successfully sent notification {} to subscription {}",
                            notification.getSequenceId(), subscription.getUuid());
                } else {
                    log.warn("Failed to send notification {} to subscription {}: HTTP {}",
                            notification.getSequenceId(), subscription.getUuid(), response.getStatusCodeValue());
                }

            } catch (Exception e) {
                log.error("Error sending notification {} to subscription {}: {}",
                        notification.getSequenceId(), subscription.getUuid(), e.getMessage(), e);
            }
        });
    }
}
