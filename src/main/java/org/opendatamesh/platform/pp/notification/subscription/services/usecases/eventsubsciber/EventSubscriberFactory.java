package org.opendatamesh.platform.pp.notification.subscription.services.usecases.eventsubsciber;

import org.opendatamesh.platform.pp.notification.subscription.services.core.SubscriptionService;
import org.opendatamesh.platform.pp.notification.utils.usecases.TransactionalOutboundPort;
import org.opendatamesh.platform.pp.notification.utils.usecases.UseCase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class EventSubscriberFactory {

    private final SubscriptionService subscriptionService;
    private final TransactionalOutboundPort transactionalOutboundPort;

    @Autowired
    public EventSubscriberFactory(SubscriptionService subscriptionService, TransactionalOutboundPort transactionalOutboundPort) {
        this.subscriptionService = subscriptionService;
        this.transactionalOutboundPort = transactionalOutboundPort;
    }

    public UseCase buildEventSubscriber(EventSubscriberCommand command, EventSubscriberPresenter presenter) {
        EventSubscriberPersistenceOutboundPort persistencePort = new EventSubscriberPersistenceOutboundPortImpl(subscriptionService);
        return new EventSubscriber(command, presenter, persistencePort, transactionalOutboundPort);
    }
}

