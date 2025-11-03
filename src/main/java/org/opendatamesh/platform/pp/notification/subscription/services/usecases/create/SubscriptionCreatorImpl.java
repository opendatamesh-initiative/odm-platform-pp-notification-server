package org.opendatamesh.platform.pp.notification.subscription.services.usecases.create;

import org.opendatamesh.platform.pp.notification.subscription.entities.Subscription;
import org.opendatamesh.platform.pp.notification.subscription.entities.SubscriptionEventType;

import java.util.ArrayList;
import java.util.List;

public class SubscriptionCreatorImpl implements SubscriptionCreator {

    private final SubscriptionCreatorPersistenceOutboundPort persistencePort;

    public SubscriptionCreatorImpl(SubscriptionCreatorPersistenceOutboundPort persistencePort) {
        this.persistencePort = persistencePort;
    }

    @Override
    public void execute(SubscriptionCreateCommand command, SubscriptionCreatorPresenter presenter) {
        try {
            Subscription subscription = new Subscription();
            subscription.setName(command.getName());
            subscription.setDisplayName(command.getDisplayName());
            subscription.setObserverServerBaseUrl(command.getObserverServerBaseUrl());

            List<SubscriptionEventType> types = new ArrayList<>();
            if (command.getEventNames() != null) {
                for (String eventName : command.getEventNames()) {
                    SubscriptionEventType t = new SubscriptionEventType();
                    t.setEventName(eventName);
                    t.setSubscription(subscription);
                    types.add(t);
                }
            }
            subscription.setEventTypes(types);

            Subscription saved = persistencePort.save(subscription);
            presenter.success(saved.getUuid().toString());
        } catch (Throwable t) {
            presenter.failure(t);
        }
    }
}


