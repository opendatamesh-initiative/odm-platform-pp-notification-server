package org.opendatamesh.platform.pp.notification.subscription.services.usecases.create;

import org.opendatamesh.platform.pp.notification.subscription.entities.Subscription;
import org.opendatamesh.platform.pp.notification.subscription.services.SubscriptionService;
import org.springframework.stereotype.Component;

@Component
public class SubscriptionCreatorPersistenceOutboundPortImpl implements SubscriptionCreatorPersistenceOutboundPort {

    private final SubscriptionService subscriptionService;

    public SubscriptionCreatorPersistenceOutboundPortImpl(SubscriptionService subscriptionService) {
        this.subscriptionService = subscriptionService;
    }

    @Override
    public Subscription save(Subscription subscription) {
        return subscriptionService.create(subscription);
    }
}


