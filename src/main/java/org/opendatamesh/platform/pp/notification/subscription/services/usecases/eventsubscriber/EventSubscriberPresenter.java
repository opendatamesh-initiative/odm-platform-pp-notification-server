package org.opendatamesh.platform.pp.notification.subscription.services.usecases.eventsubscriber;

import org.opendatamesh.platform.pp.notification.subscription.entities.Subscription;

public interface EventSubscriberPresenter {
    void presentUpdatedSubscription(Subscription subscription);
}
