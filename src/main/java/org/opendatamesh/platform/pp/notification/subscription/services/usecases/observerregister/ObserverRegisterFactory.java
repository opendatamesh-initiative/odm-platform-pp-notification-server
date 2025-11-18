package org.opendatamesh.platform.pp.notification.subscription.services.usecases.observerregister;

import org.opendatamesh.platform.pp.notification.subscription.services.core.SubscriptionService;
import org.opendatamesh.platform.pp.notification.utils.usecases.TransactionalOutboundPort;
import org.opendatamesh.platform.pp.notification.utils.usecases.UseCase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ObserverRegisterFactory {

    private final SubscriptionService subscriptionService;
    private final TransactionalOutboundPort transactionalOutboundPort;

    @Autowired
    public ObserverRegisterFactory(SubscriptionService subscriptionService, TransactionalOutboundPort transactionalOutboundPort) {
        this.subscriptionService = subscriptionService;
        this.transactionalOutboundPort = transactionalOutboundPort;
    }

    public UseCase buildObserverRegister(ObserverRegisterCommand command, ObserverRegisterPresenter presenter) {
        ObserverRegisterPersistenceOutboundPort persistencePort = new ObserverRegisterPersistenceOutboundPortImpl(subscriptionService);
        return new ObserverRegister(command, presenter, persistencePort, transactionalOutboundPort);
    }
}

