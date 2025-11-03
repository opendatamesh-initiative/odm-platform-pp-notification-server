package org.opendatamesh.platform.pp.notification.subscription.services.usecases.create;

import org.opendatamesh.platform.pp.notification.subscription.entities.Subscription;

public interface SubscriptionCreatorPersistenceOutboundPort {
    Subscription save(Subscription subscription);
}


