package org.opendatamesh.platform.pp.notification.subscription.services.usecases.eventsubscriber;

import java.util.Set;

public record EventSubscriberCommand(
        String observerName,
        Set<String> eventTypes
) {
}
