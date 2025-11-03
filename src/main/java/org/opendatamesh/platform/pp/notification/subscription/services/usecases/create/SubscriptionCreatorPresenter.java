package org.opendatamesh.platform.pp.notification.subscription.services.usecases.create;

public interface SubscriptionCreatorPresenter {
    void success(String subscriptionUuid);
    void failure(Throwable error);
}


