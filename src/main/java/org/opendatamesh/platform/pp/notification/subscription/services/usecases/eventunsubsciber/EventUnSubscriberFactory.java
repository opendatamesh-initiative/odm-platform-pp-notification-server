package org.opendatamesh.platform.pp.notification.subscription.services.usecases.eventunsubsciber;

import org.opendatamesh.platform.pp.notification.subscription.services.core.SubscriptionService;
import org.opendatamesh.platform.pp.notification.utils.usecases.TransactionalOutboundPort;
import org.opendatamesh.platform.pp.notification.utils.usecases.UseCase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class EventUnSubscriberFactory {

    private final SubscriptionService subscriptionService;
    private final TransactionalOutboundPort transactionalOutboundPort;

    @Autowired
    public EventUnSubscriberFactory(SubscriptionService subscriptionService, TransactionalOutboundPort transactionalOutboundPort) {
        this.subscriptionService = subscriptionService;
        this.transactionalOutboundPort = transactionalOutboundPort;
    }

    public UseCase buildEventUnSubscriber(EventUnSubscriberCommand command, EventUnSubscriberPresenter presenter) {
        EventUnSubscriberPersistenceOutboundPort persistencePort = new EventUnSubscriberPersistenceOutboundPortImpl(subscriptionService);
        return new EventUnSubscriber(command, presenter, persistencePort, transactionalOutboundPort);
    }
}

