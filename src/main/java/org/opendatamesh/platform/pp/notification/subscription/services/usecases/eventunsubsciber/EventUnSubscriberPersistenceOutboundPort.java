package org.opendatamesh.platform.pp.notification.subscription.services.usecases.eventunsubsciber;

import org.opendatamesh.platform.pp.notification.subscription.entities.Subscription;

import java.util.Optional;

interface EventUnSubscriberPersistenceOutboundPort {
    Optional<Subscription> findSubscriptionByName(String name);

    void save(Subscription subscription);
}
