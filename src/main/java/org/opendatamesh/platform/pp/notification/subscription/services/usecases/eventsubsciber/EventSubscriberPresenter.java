package org.opendatamesh.platform.pp.notification.subscription.services.usecases.eventsubsciber;

import org.opendatamesh.platform.pp.notification.subscription.entities.Subscription;

public interface EventSubscriberPresenter {
    void presentUpdatedSubscription(Subscription subscription);
}
