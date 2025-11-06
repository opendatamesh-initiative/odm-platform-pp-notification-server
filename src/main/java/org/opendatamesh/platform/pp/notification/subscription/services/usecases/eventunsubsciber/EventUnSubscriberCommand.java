package org.opendatamesh.platform.pp.notification.subscription.services.usecases.eventunsubsciber;

import java.util.Set;

public record EventUnSubscriberCommand(
        String observerName,
        Set<String> eventTypes
) {
}
