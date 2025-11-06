package org.opendatamesh.platform.pp.notification.subscription.services.usecases.eventsubsciber;

import org.opendatamesh.platform.pp.notification.subscription.entities.Subscription;

import java.util.Optional;

interface EventSubscriberPersistenceOutboundPort {
    Optional<Subscription> findSubscriptionByName(String name);

    void save(Subscription subscription);
}
