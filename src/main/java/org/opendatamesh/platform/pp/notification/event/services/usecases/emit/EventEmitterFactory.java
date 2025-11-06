package org.opendatamesh.platform.pp.notification.event.services.usecases.emit;

import org.opendatamesh.platform.pp.notification.client.ObserverClient;
import org.opendatamesh.platform.pp.notification.event.services.core.EventService;
import org.opendatamesh.platform.pp.notification.notification.services.core.NotificationService;
import org.opendatamesh.platform.pp.notification.subscription.services.core.SubscriptionService;
import org.opendatamesh.platform.pp.notification.utils.services.programmatic.AsyncExecutorService;
import org.opendatamesh.platform.pp.notification.utils.services.programmatic.EntityInitAndDetachService;
import org.opendatamesh.platform.pp.notification.utils.usecases.TransactionalOutboundPort;
import org.opendatamesh.platform.pp.notification.utils.usecases.UseCase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class EventEmitterFactory {

    private final EventService eventService;
    private final SubscriptionService subscriptionService;
    private final NotificationService notificationService;
    private final TransactionalOutboundPort transactionalOutboundPort;
    private final AsyncExecutorService asyncExecutorService;
    private final EntityInitAndDetachService entityInitAndDetachService;
    private final ObserverClient observerClient;

    @Autowired
    public EventEmitterFactory(
            EventService eventService,
            SubscriptionService subscriptionService,
            NotificationService notificationService,
            TransactionalOutboundPort transactionalOutboundPort,
            AsyncExecutorService asyncExecutorService,
            EntityInitAndDetachService entityInitAndDetachService,
            ObserverClient observerClient) {
        this.eventService = eventService;
        this.subscriptionService = subscriptionService;
        this.notificationService = notificationService;
        this.transactionalOutboundPort = transactionalOutboundPort;
        this.asyncExecutorService = asyncExecutorService;
        this.entityInitAndDetachService = entityInitAndDetachService;
        this.observerClient = observerClient;
    }

    public UseCase buildEventEmitter(EventEmitterCommand command, EventEmitterPresenter presenter) {
        EventEmitterPersistenceOutboundPort persistencePort = new EventEmitterPersistenceOutboundPortImpl(eventService);
        EventEmitterSubscriptionOutboundPort subscriptionPort = new EventEmitterSubscriptionOutboundPortImpl(subscriptionService);
        EventEmitterNotificationOutboundPort notificationPort = new EventEmitterNotificationOutboundPortImpl(notificationService);

        return new EventEmitter(
                command,
                presenter,
                transactionalOutboundPort,
                asyncExecutorService,
                entityInitAndDetachService,
                persistencePort,
                subscriptionPort,
                notificationPort,
                observerClient
        );
    }
}

