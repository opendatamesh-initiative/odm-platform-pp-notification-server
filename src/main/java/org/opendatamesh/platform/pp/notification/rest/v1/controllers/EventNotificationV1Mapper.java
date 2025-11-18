package org.opendatamesh.platform.pp.notification.rest.v1.controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.opendatamesh.platform.pp.notification.event.entities.Event;
import org.opendatamesh.platform.pp.notification.notification.entities.Notification;
import org.opendatamesh.platform.pp.notification.notification.entities.NotificationStatus;
import org.opendatamesh.platform.pp.notification.rest.v1.resources.EventNotificationResV1;
import org.opendatamesh.platform.pp.notification.rest.v1.resources.EventNotificationStatusResV1;
import org.opendatamesh.platform.pp.notification.rest.v1.resources.EventNotificationV1SearchOptions;
import org.opendatamesh.platform.pp.notification.rest.v1.resources.EventResV1;
import org.opendatamesh.platform.pp.notification.rest.v1.resources.EventV1SearchOptions;
import org.opendatamesh.platform.pp.notification.rest.v1.resources.ObserverResV1;
import org.opendatamesh.platform.pp.notification.rest.v2.resources.event.EventSearchOptions;
import org.opendatamesh.platform.pp.notification.rest.v2.resources.notification.NotificationSearchOptions;
import org.opendatamesh.platform.pp.notification.rest.v2.resources.notification.NotificationStatusRes;
import org.opendatamesh.platform.pp.notification.subscription.entities.Subscription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.sql.Date;

@Component
public class EventNotificationV1Mapper {

    private static final Logger log = LoggerFactory.getLogger(EventNotificationV1Mapper.class);
    private final ObjectMapper objectMapper;

    public EventNotificationV1Mapper() {
        this.objectMapper = new ObjectMapper();
    }

    public EventNotificationResV1 toRes(Notification notification) {
        EventNotificationResV1 eventNotificationResV1 = new EventNotificationResV1();
        eventNotificationResV1.setId(notification.getSequenceId());
        if (notification.getEvent() != null) {
            eventNotificationResV1.setEvent(toEventResV1(notification.getEvent()));
        }
        eventNotificationResV1.setStatus(mapNotificationStatus(notification.getStatus()));
        eventNotificationResV1.setProcessingOutput(
                notification.getErrorMessage() != null ? notification.getErrorMessage() : ""
        );
        if (notification.getSubscription() != null) {
            eventNotificationResV1.setObserver(toObserverResV1(notification.getSubscription()));
        }
        if (notification.getCreatedAt() != null) {
            eventNotificationResV1.setReceivedAt(new Date(notification.getCreatedAt().getTime()));
        }
        if (notification.getUpdatedAt() != null) {
            eventNotificationResV1.setProcessedAt(new Date(notification.getUpdatedAt().getTime()));
        }
        return eventNotificationResV1;
    }

    public EventResV1 toEventResV1(Event event) {
        EventResV1 eventResV1 = new EventResV1();
        eventResV1.setId(event.getSequenceId());
        eventResV1.setType(event.getType());
        eventResV1.setEntityId(event.getResourceIdentifier());
        // Parse eventContent JSON to extract beforeState and afterState
        if (event.getEventContent() != null && !event.getEventContent().isEmpty()) {
            try {
                JsonNode eventContentJson = objectMapper.readTree(event.getEventContent());

                if (eventContentJson.has("beforeState")) {
                    eventResV1.setBeforeState(eventContentJson.get("beforeState"));
                }
                if (eventContentJson.has("afterState")) {
                    eventResV1.setAfterState(eventContentJson.get("afterState"));
                }
            } catch (Exception e) {
                log.warn("Failed to parse eventContent JSON for event {}: {}",
                        event.getSequenceId(), e.getMessage());
            }
        }
        if (event.getCreatedAt() != null) {
            eventResV1.setTime(new Date(event.getCreatedAt().getTime()));
        }
        return eventResV1;
    }

    private ObserverResV1 toObserverResV1(Subscription subscription) {
        ObserverResV1 observerResV1 = new ObserverResV1();
        observerResV1.setId(null);
        observerResV1.setName(subscription.getName());
        observerResV1.setDisplayName(subscription.getDisplayName());
        observerResV1.setObserverServerBaseUrl(subscription.getObserverServerBaseUrl());
        return observerResV1;
    }

    private EventNotificationStatusResV1 mapNotificationStatus(NotificationStatus status) {
        if (status == null) {
            return null;
        }
        return switch (status) {
            case PROCESSING -> EventNotificationStatusResV1.PROCESSING;
            case PROCESSED -> EventNotificationStatusResV1.PROCESSED;
            case FAILED_TO_DELIVER -> EventNotificationStatusResV1.UNPROCESSABLE;
            case FAILED_TO_PROCESS -> EventNotificationStatusResV1.PROCESS_ERROR;
        };
    }

    public NotificationStatusRes mapToNotificationStatusRes(EventNotificationStatusResV1 status) {
        if (status == null) {
            return null;
        }
        return switch (status) {
            case PROCESSING -> NotificationStatusRes.PROCESSING;
            case PROCESSED -> NotificationStatusRes.PROCESSED;
            case UNPROCESSABLE -> NotificationStatusRes.FAILED_TO_DELIVER;
            case PROCESS_ERROR -> NotificationStatusRes.FAILED_TO_PROCESS;
        };
    }

    public NotificationSearchOptions toNotificationSearchOptions(EventNotificationV1SearchOptions searchOptions) {
        if (searchOptions == null) {
            return new NotificationSearchOptions();
        }
        NotificationSearchOptions notificationSearchOptions = new NotificationSearchOptions();
        notificationSearchOptions.setEventType(searchOptions.getEventType());
        if (searchOptions.getNotificationStatus() != null) {
            NotificationStatusRes statusRes = mapToNotificationStatusRes(searchOptions.getNotificationStatus());
            notificationSearchOptions.setNotificationStatus(statusRes != null ? statusRes.name() : null);
        }
        return notificationSearchOptions;
    }

    public EventSearchOptions toEventSearchOptions(EventV1SearchOptions searchOptions) {
        if (searchOptions == null) {
            return new EventSearchOptions();
        }
        EventSearchOptions eventSearchOptions = new EventSearchOptions();
        eventSearchOptions.setEventType(searchOptions.getEventType());
        // Map entityId to resourceUuid in v2
        if (searchOptions.getEntityId() != null) {
            eventSearchOptions.setResourceUuid(searchOptions.getEntityId());
        }
        return eventSearchOptions;
    }
}

