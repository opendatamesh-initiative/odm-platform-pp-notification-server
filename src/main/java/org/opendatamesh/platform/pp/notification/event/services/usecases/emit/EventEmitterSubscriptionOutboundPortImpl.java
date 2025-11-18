package org.opendatamesh.platform.pp.notification.event.services.usecases.emit;

import org.opendatamesh.platform.pp.notification.rest.v2.resources.subscription.SubscriptionSearchOptions;
import org.opendatamesh.platform.pp.notification.subscription.entities.Subscription;
import org.opendatamesh.platform.pp.notification.subscription.services.core.SubscriptionService;
import org.springframework.data.domain.Pageable;

import java.util.List;

class EventEmitterSubscriptionOutboundPortImpl implements EventEmitterSubscriptionOutboundPort {

    private final SubscriptionService subscriptionService;

    EventEmitterSubscriptionOutboundPortImpl(SubscriptionService subscriptionService) {
        this.subscriptionService = subscriptionService;
    }

    @Override
    public List<Subscription> findGenericSubscription() {
        SubscriptionSearchOptions filters = new SubscriptionSearchOptions();
        filters.setWithoutEventTypes(true);
        return subscriptionService.findAllFiltered(Pageable.unpaged(), filters).toList();
    }

    @Override
    public List<Subscription> findSubscriptionByEventType(String type) {
        SubscriptionSearchOptions filters = new SubscriptionSearchOptions();
        filters.setEventTypeName(type);
        return subscriptionService.findAllFiltered(Pageable.unpaged(), filters).toList();
    }
}
