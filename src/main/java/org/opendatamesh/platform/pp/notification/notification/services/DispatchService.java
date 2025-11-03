package org.opendatamesh.platform.pp.notification.notification.services;


import org.opendatamesh.platform.pp.notification.event.entities.Event;
import org.opendatamesh.platform.pp.notification.event.services.EventService;
import org.opendatamesh.platform.pp.notification.exceptions.BadRequestException;
import org.opendatamesh.platform.pp.notification.notification.entities.Notification;
import org.opendatamesh.platform.pp.notification.rest.v2.resources.event.EventRes;
import org.opendatamesh.platform.pp.notification.rest.v2.resources.notification.NotificationRes;
import org.opendatamesh.platform.pp.notification.rest.v2.resources.notification.NotificationStatus;
import org.opendatamesh.platform.pp.notification.rest.v2.resources.subscription.SubscriptionEventTypeRes;
import org.opendatamesh.platform.pp.notification.rest.v2.resources.subscription.SubscriptionRes;
import org.opendatamesh.platform.pp.notification.subscription.entities.Subscription;
import org.opendatamesh.platform.pp.notification.subscription.services.SubscriptionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DispatchService {

    private static final Logger log = LoggerFactory.getLogger(DispatchService.class);

    @Autowired
    private SubscriptionService subscriptionService;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private EventService eventService;

    @Autowired
    private NotificationClient deliveryClient;

    public void notifyAll(EventRes eventToDispatch) {
        if (eventToDispatch == null) {
            throw new BadRequestException("Event cannot be null");
        }
        log.info("Dispatching event: {} - Resource: {}", eventToDispatch.getType(), eventToDispatch.getResourceIdentifier());

        // 1. Create Event
        EventRes event = eventService.createResource(eventToDispatch);
        log.info("Created event with ID: {}", event.getSequenceId());

        // 2. Get event type as string for filtering
        String eventType = event.getType() != null ? event.getType().name() : null;

        // 3. Find all subscriptions (page by page)
        PageRequest pageRequest = PageRequest.of(0, 20);
        Page<SubscriptionRes> subscriptionPage = subscriptionService.findAllResources(pageRequest);
        notifyAllSlice(subscriptionPage.getContent(), event, eventType);

        while (subscriptionPage.hasNext()) {
            subscriptionPage = subscriptionService.findAllResources(subscriptionPage.nextPageable());
            notifyAllSlice(subscriptionPage.getContent(), event, eventType);
        }

        log.info("Event dispatch completed for event ID: {}", event.getSequenceId());
    }

    private void notifyAllSlice(List<SubscriptionRes> subscriptions, EventRes event, String eventType) {
        subscriptions.forEach(subscription -> notifyOne(subscription, event, eventType));
    }

    private void notifyOne(SubscriptionRes subscription, EventRes event, String eventType) {
        // Filter: only notify if subscription is subscribed to this event type
        if (!isSubscriptionInterestedInEvent(subscription, eventType)) {
            log.debug("Subscription {} not interested in event type {}",
                    subscription.getUuid(), eventType);
            return;
        }

        try {
            log.info("Notifying subscription: {} for event: {}", subscription.getName(), event.getSequenceId());

            // Create notification with DELIVERED status
            NotificationRes notification = createNotification(subscription, event);

            // Dispatch Notification to subscription (async + isolated)
            deliveryClient.dispatchNotificationToSubscription(subscription, notification);

            log.info("Notification created and dispatched for subscription: {}", subscription.getUuid());

        } catch (Exception e) {
            log.error("Error notifying subscription: {} - Error: {}", subscription.getUuid(), e.getMessage(), e);
            // Create notification with FAILED status
            createFailedNotification(subscription, event, e.getMessage());
        }
    }

    private boolean isSubscriptionInterestedInEvent(SubscriptionRes subscription, String eventType) {
        List<SubscriptionEventTypeRes> eventTypes = subscription.getEventTypes();

        // If no event types specified or empty list, this subscription is interested in ALL events
        if (eventTypes == null || eventTypes.isEmpty()) {
            return true;
        }

        // Check if the event type is in the subscription's interest list
        return eventTypes.contains(eventType);
    }

    private NotificationRes createNotification(SubscriptionRes subscription, EventRes event) {
        NotificationRes notification = new NotificationRes();
        notification.setStatus(NotificationStatus.DELIVERED);
        // Set event
        notification.setEvent(event);
        // Set subscription
        notification.setSubscription(subscription);
        // Save notification
        notification = notificationService.createResource(notification);
        return notification;
    }

    private void createFailedNotification(SubscriptionRes subscription, EventRes event, String errorMessage) {
        try {
            Notification notification = new Notification();
            notification.setStatus(NotificationStatus.FAILED);
            notification.setErrorMessage(errorMessage);

            Event eventEntity = new Event();
            eventEntity.setSequenceId(event.getSequenceId());
            notification.setEvent(eventEntity);

            Subscription subscriptionEntity = new Subscription();
            subscriptionEntity.setUuid(subscription.getUuid());
            notification.setSubscription(subscriptionEntity);

            notificationService.create(notification);
        } catch (Exception e) {
            log.error("Failed to create FAILED notification record", e);
        }
    }
}
