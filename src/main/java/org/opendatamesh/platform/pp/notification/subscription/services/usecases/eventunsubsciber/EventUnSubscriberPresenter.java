package org.opendatamesh.platform.pp.notification.subscription.services.usecases.eventunsubsciber;

import org.opendatamesh.platform.pp.notification.subscription.entities.Subscription;

public interface EventUnSubscriberPresenter {
    void presentUpdatedSubscription(Subscription subscription);
}
