package org.opendatamesh.platform.pp.notification.subscription.services.usecases.observerregister;

import org.opendatamesh.platform.pp.notification.subscription.entities.Subscription;

import java.util.Optional;

interface ObserverRegisterPersistenceOutboundPort {
    Optional<Subscription> findSubscriptionByName(String name);

    void save(Subscription subscription);

    Subscription create(Subscription subscription);
}
