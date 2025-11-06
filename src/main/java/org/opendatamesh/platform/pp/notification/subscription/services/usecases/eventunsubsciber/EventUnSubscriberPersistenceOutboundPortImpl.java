package org.opendatamesh.platform.pp.notification.subscription.services.usecases.eventunsubsciber;

import org.opendatamesh.platform.pp.notification.rest.v2.resources.subscription.SubscriptionSearchOptions;
import org.opendatamesh.platform.pp.notification.subscription.entities.Subscription;
import org.opendatamesh.platform.pp.notification.subscription.services.core.SubscriptionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

class EventUnSubscriberPersistenceOutboundPortImpl implements EventUnSubscriberPersistenceOutboundPort {

    private final SubscriptionService subscriptionService;

    EventUnSubscriberPersistenceOutboundPortImpl(SubscriptionService subscriptionService) {
        this.subscriptionService = subscriptionService;
    }

    @Override
    public Optional<Subscription> findSubscriptionByName(String name) {
        SubscriptionSearchOptions searchOptions = new SubscriptionSearchOptions();
        searchOptions.setName(name);
        Pageable pageable = PageRequest.of(0, 1);
        Page<Subscription> page = subscriptionService.findAllFiltered(pageable, searchOptions);
        return page.getContent().stream().findFirst();
    }

    @Override
    public void save(Subscription subscription) {
        subscriptionService.overwrite(subscription.getUuid(), subscription);
    }
}

