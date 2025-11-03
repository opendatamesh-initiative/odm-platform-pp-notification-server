package org.opendatamesh.platform.pp.notification.subscription.services.usecases.create;

import java.util.List;

public class SubscriptionCreateCommand {
    private final String name;
    private final String displayName;
    private final String observerServerBaseUrl;
    private final List<String> eventNames;

    public SubscriptionCreateCommand(String name,
                                     String displayName,
                                     String observerServerBaseUrl,
                                     List<String> eventNames) {
        this.name = name;
        this.displayName = displayName;
        this.observerServerBaseUrl = observerServerBaseUrl;
        this.eventNames = eventNames;
    }

    public String getName() {
        return name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getObserverServerBaseUrl() {
        return observerServerBaseUrl;
    }

    public List<String> getEventNames() {
        return eventNames;
    }
}


