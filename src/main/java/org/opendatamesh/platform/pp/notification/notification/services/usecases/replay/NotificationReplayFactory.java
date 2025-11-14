package org.opendatamesh.platform.pp.notification.notification.services.usecases.replay;

import org.opendatamesh.platform.pp.notification.client.ObserverClient;
import org.opendatamesh.platform.pp.notification.notification.services.core.NotificationService;
import org.opendatamesh.platform.pp.notification.utils.services.programmatic.EntityInitAndDetachService;
import org.opendatamesh.platform.pp.notification.utils.usecases.TransactionalOutboundPort;
import org.opendatamesh.platform.pp.notification.utils.usecases.UseCase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class NotificationReplayFactory {

    private final NotificationService notificationService;
    private final TransactionalOutboundPort transactionalOutboundPort;
    private final EntityInitAndDetachService entityInitAndDetachService;
    private final ObserverClient observerClient;

    @Autowired
    public NotificationReplayFactory(
            NotificationService notificationService,
            TransactionalOutboundPort transactionalOutboundPort,
            EntityInitAndDetachService entityInitAndDetachService,
            ObserverClient observerClient) {
        this.notificationService = notificationService;
        this.transactionalOutboundPort = transactionalOutboundPort;
        this.entityInitAndDetachService = entityInitAndDetachService;
        this.observerClient = observerClient;
    }

    public UseCase buildNotificationReplay(NotificationReplayCommand command, NotificationReplayPresenter presenter) {
        NotificationReplayPersistenceOutboundPort persistencePort = new NotificationReplayPersistenceOutboundPortImpl(notificationService);
        NotificationReplayObserverOutboundPort observerPort = new NotificationReplayObserverOutboundPortImpl(observerClient);

        return new NotificationReplay(
                command,
                presenter,
                transactionalOutboundPort,
                entityInitAndDetachService,
                persistencePort,
                observerPort
        );
    }
}

