package org.opendatamesh.platform.pp.notification.subscription.services.usecases.eventunsubscriber;

import org.opendatamesh.platform.pp.notification.exceptions.BadRequestException;
import org.opendatamesh.platform.pp.notification.exceptions.NotFoundException;
import org.opendatamesh.platform.pp.notification.subscription.entities.Subscription;
import org.opendatamesh.platform.pp.notification.subscription.entities.SubscriptionEventType;
import org.opendatamesh.platform.pp.notification.utils.usecases.TransactionalOutboundPort;
import org.opendatamesh.platform.pp.notification.utils.usecases.UseCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * Use case for unsubscribing an observer from one or more event types.
 * <p>
 * This use case unsubscribes an already registered observer from one or more
 * event types. By default, a newly registered observer is subscribed to ALL
 * events.
 * </p>
 */
class EventUnSubscriber implements UseCase {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private static final String USE_CASE_PREFIX = "[EventUnSubscriber]";

    private final EventUnSubscriberCommand command;
    private final EventUnSubscriberPresenter presenter;

    private final EventUnSubscriberPersistenceOutboundPort persistencePort;
    private final TransactionalOutboundPort transactionalOutboundPort;

    EventUnSubscriber(EventUnSubscriberCommand command, EventUnSubscriberPresenter presenter, EventUnSubscriberPersistenceOutboundPort persistencePort, TransactionalOutboundPort transactionalOutboundPort) {
        this.command = command;
        this.presenter = presenter;
        this.persistencePort = persistencePort;
        this.transactionalOutboundPort = transactionalOutboundPort;
    }

    @Override
    public void execute() {
        validateCommand();
        transactionalOutboundPort.doInTransaction(() -> {
            Subscription subscription = persistencePort.findSubscriptionByName(command.observerName())
                    .orElseThrow(() -> new NotFoundException("Observer '" + command.observerName() + "' does not exist."));

            Set<String> eventTypes = subscription.getEventTypes().stream().map(SubscriptionEventType::getEventType).collect(Collectors.toSet());
            logger.info("{} Observer: '{}' currently subscribed to: '{}' event types.", USE_CASE_PREFIX, subscription.getName(), eventTypes.isEmpty() ? "ALL" : eventTypes);

            // Remove event types that match the command (to unsubscribe from them)
            // We must modify the existing collection, not replace it, due to orphanRemoval = true
            subscription.getEventTypes().removeIf(event -> command.eventTypes().contains(event.getEventType()));
            persistencePort.save(subscription);
            logger.info("{} Observer: '{}' now subscribed to: '{}' event types.", USE_CASE_PREFIX, subscription.getName(), eventTypes.isEmpty() ? "ALL" : eventTypes);

            presenter.presentUpdatedSubscription(subscription);
        });
    }

    private void validateCommand() {
        if (command == null) {
            throw new BadRequestException("Command cannot be null");
        }
        if (!StringUtils.hasText(command.observerName())) {
            throw new BadRequestException("Observer name cannot be null or empty");
        }
        if (command.eventTypes() == null || command.eventTypes().isEmpty()) {
            throw new BadRequestException("Event types cannot be null or empty");
        }
    }
}
