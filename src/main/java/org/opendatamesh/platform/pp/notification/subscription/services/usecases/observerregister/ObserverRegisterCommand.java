package org.opendatamesh.platform.pp.notification.subscription.services.usecases.observerregister;

public record ObserverRegisterCommand(
        String observerName,
        String observerDisplayName,
        String observerBaseUrl,
        String observerApiVersion
) {
}
