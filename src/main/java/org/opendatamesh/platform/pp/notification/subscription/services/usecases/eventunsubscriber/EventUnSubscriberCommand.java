package org.opendatamesh.platform.pp.notification.subscription.services.usecases.eventunsubscriber;

import java.util.Set;

public record EventUnSubscriberCommand(
        String observerName,
        Set<String> eventTypes
) {
}
