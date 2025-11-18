package org.opendatamesh.platform.pp.notification.subscription.services.usecases.eventunsubscriber;

import org.opendatamesh.platform.pp.notification.subscription.entities.Subscription;

public interface EventUnSubscriberPresenter {
    void presentUpdatedSubscription(Subscription subscription);
}
