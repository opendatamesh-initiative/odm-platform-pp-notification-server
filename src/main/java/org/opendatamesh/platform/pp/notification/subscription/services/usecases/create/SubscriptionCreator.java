package org.opendatamesh.platform.pp.notification.subscription.services.usecases.create;

public interface SubscriptionCreator {
    void execute(SubscriptionCreateCommand command, SubscriptionCreatorPresenter presenter);
}


