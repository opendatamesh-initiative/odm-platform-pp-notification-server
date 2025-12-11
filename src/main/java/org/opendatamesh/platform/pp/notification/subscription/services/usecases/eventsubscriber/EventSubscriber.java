package org.opendatamesh.platform.pp.notification.subscription.services.usecases.eventsubscriber;

import org.opendatamesh.platform.pp.notification.exceptions.BadRequestException;
import org.opendatamesh.platform.pp.notification.exceptions.NotFoundException;
import org.opendatamesh.platform.pp.notification.subscription.entities.Subscription;
import org.opendatamesh.platform.pp.notification.subscription.entities.SubscriptionEventType;
import org.opendatamesh.platform.pp.notification.utils.usecases.TransactionalOutboundPort;
import org.opendatamesh.platform.pp.notification.utils.usecases.UseCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Use case for subscribing an observer to one or more event types.
 * <p>
 * This use case subscribes an already registered observer to one or more
 * event types. If an observer is not subscribed to any specific event type,
 * it is subscribed to ALL events.
 * </p>
 */
class EventSubscriber implements UseCase {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private static final String USE_CASE_PREFIX = "[EventSubscriber]";

    private final EventSubscriberCommand command;
    private final EventSubscriberPresenter presenter;

    private final EventSubscriberPersistenceOutboundPort persistencePort;
    private final TransactionalOutboundPort transactionalOutboundPort;

    EventSubscriber(EventSubscriberCommand command, EventSubscriberPresenter presenter, EventSubscriberPersistenceOutboundPort persistencePort, TransactionalOutboundPort transactionalOutboundPort) {
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
                    .orElseThrow(() -> new NotFoundException("Observer " + command.observerName() + " does not exist."));

            Set<String> eventTypes = subscription.getEventTypes().stream().map(SubscriptionEventType::getEventType).collect(Collectors.toSet());
            logger.info("{} Observer: '{}' currently subscribed to: '{}' event types.", USE_CASE_PREFIX, subscription.getName(), eventTypes.isEmpty() ? "ALL" : eventTypes);

            List<SubscriptionEventType> subscriptionEventTypes = subscription.getEventTypes();
            command.eventTypes().stream()
                    .filter(event -> !eventTypes.contains(event))
                    .forEach(event -> {
                        SubscriptionEventType newEventType = new SubscriptionEventType(event);
                        newEventType.setSubscription(subscription);
                        subscriptionEventTypes.add(newEventType);
                        eventTypes.add(event);
                    });

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
