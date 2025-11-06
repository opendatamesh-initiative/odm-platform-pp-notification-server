package org.opendatamesh.platform.pp.notification.subscription.services.usecases.eventsubsciber;

import java.util.Set;

public record EventSubscriberCommand(
        String observerName,
        Set<String> eventTypes
) {
}
