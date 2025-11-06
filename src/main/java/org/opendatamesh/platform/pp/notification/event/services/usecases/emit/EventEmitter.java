package org.opendatamesh.platform.pp.notification.event.services.usecases.emit;

import org.opendatamesh.platform.pp.notification.client.ObserverClient;
import org.opendatamesh.platform.pp.notification.event.entities.Event;
import org.opendatamesh.platform.pp.notification.exceptions.BadRequestException;
import org.opendatamesh.platform.pp.notification.notification.entities.Notification;
import org.opendatamesh.platform.pp.notification.notification.entities.NotificationStatus;
import org.opendatamesh.platform.pp.notification.subscription.entities.Subscription;
import org.opendatamesh.platform.pp.notification.utils.services.programmatic.AsyncExecutorService;
import org.opendatamesh.platform.pp.notification.utils.services.programmatic.EntityInitAndDetachService;
import org.opendatamesh.platform.pp.notification.utils.usecases.TransactionalOutboundPort;
import org.opendatamesh.platform.pp.notification.utils.usecases.UseCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * Use case for Emitting an event
 * <p>
 * This use case handles the emission of an event from an ODM service.
 * </p>
 */
class EventEmitter implements UseCase {

    private static final String USE_CASE_PREFIX = "[EventEmitter]";
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final EventEmitterCommand command;
    private final EventEmitterPresenter presenter;

    private final TransactionalOutboundPort transactionalPort;
    private final AsyncExecutorService asyncExecutor;
    private final EntityInitAndDetachService entityDetacher;

    private final EventEmitterPersistenceOutboundPort persistencePort;
    private final EventEmitterSubscriptionOutboundPort subscriptionPort;
    private final EventEmitterNotificationOutboundPort notificationPort;

    private final ObserverClient observerClient;

    EventEmitter(EventEmitterCommand command, EventEmitterPresenter presenter, TransactionalOutboundPort transactionalPort, AsyncExecutorService asyncExecutor, EntityInitAndDetachService entityDetacher, EventEmitterPersistenceOutboundPort persistencePort, EventEmitterSubscriptionOutboundPort subscriptionPort, EventEmitterNotificationOutboundPort notificationPort, ObserverClient observerClient) {
        this.command = command;
        this.presenter = presenter;
        this.transactionalPort = transactionalPort;
        this.asyncExecutor = asyncExecutor;
        this.entityDetacher = entityDetacher;
        this.persistencePort = persistencePort;
        this.subscriptionPort = subscriptionPort;
        this.notificationPort = notificationPort;
        this.observerClient = observerClient;
    }

    @Override
    public void execute() {
        validateCommand();
        logger.info("{} Starting event emission for event type: {}", USE_CASE_PREFIX, command.event().getType());

        Event event = createEvent();

        asyncExecutor.execute(() -> {
            //Handle subscriptions with no specific event type (subscribed to ALL events)
            List<Subscription> genericSubscriptions = subscriptionPort.findGenericSubscription();
            processSubscriptions(genericSubscriptions, event);

            //Handle subscriptions on the specific event type
            List<Subscription> specificSubscriptions = subscriptionPort.findSubscriptionByEventType(command.event().getType());
            processSubscriptions(specificSubscriptions, event);

            logger.info("{} Completed processing all subscriptions for event: {}", USE_CASE_PREFIX, event.getSequenceId());
        });
    }

    private Event createEvent() {
        Event event = transactionalPort.doInTransactionWithResults((Void v) -> {
            Event savedEvent = persistencePort.create(command.event());
            entityDetacher.initializeEntityAndDetach(savedEvent);
            return savedEvent;
        }, null);
        presenter.presentCreatedEvent(event);
        return event;
    }

    private void processSubscriptions(List<Subscription> subscriptions, Event event) {
        for (Subscription subscription : subscriptions) {
            logger.info("{} Processing subscription: {} for event: {}", USE_CASE_PREFIX, subscription.getDisplayName(), event.getSequenceId());
            // Step 1: Create notification with PROCESSING status (transaction commits)
            Notification notification = createNotification(event, subscription);
            // Step 2: Dispatch to observer (outside transaction, after commit)
            try {
                observerClient.dispatchNotification(notification);
                logger.info("{} Successfully dispatched notification {} to observer: {} at {}",
                        USE_CASE_PREFIX, notification.getSequenceId(), subscription.getDisplayName(), subscription.getObserverServerBaseUrl());
                // Note: We don't update the notification here to avoid race condition with observer
                // Only update on failure to avoid conflicts with observer's status updates
            } catch (Exception e) {
                // Step 3: Update status to FAILED_TO_DELIVER only on dispatch failure
                // This is safe because observer won't update a notification that failed to be delivered
                logger.warn("{} Error when dispatching event to: {}, error: {}", USE_CASE_PREFIX, subscription.getDisplayName(), e.getMessage(), e);
                handleNotificationFailure(e.getMessage(), notification);
            }
        }
    }

    private Notification createNotification(Event event, Subscription subscription) {
        return transactionalPort.doInTransactionWithResults((Void v) -> {
            Notification newNotification = new Notification();
            newNotification.setEvent(event);
            newNotification.setSubscription(subscription);
            newNotification.setStatus(NotificationStatus.PROCESSING);
            newNotification = notificationPort.create(newNotification);
            entityDetacher.initializeEntityAndDetach(newNotification);
            return newNotification;
        }, null);
    }

    private void handleNotificationFailure(String errorMessage, Notification notification) {
        transactionalPort.doInTransaction(() -> {
            Notification currentNotification = notificationPort.findById(notification.getSequenceId());
            if (currentNotification != null && currentNotification.getStatus() == NotificationStatus.PROCESSING) {
                currentNotification.setStatus(NotificationStatus.FAILED_TO_DELIVER);
                currentNotification.setErrorMessage(errorMessage);
                notificationPort.update(currentNotification);
            }
        });
    }

    private void validateCommand() {
        if (command == null) {
            throw new BadRequestException("Command cannot be null");
        }
        if (command.event() == null) {
            throw new BadRequestException("Event cannot be null");
        }
        if (command.event().getType() == null) {
            throw new BadRequestException("Event type cannot be null");
        }
        if (!StringUtils.hasText(command.event().getEventTypeVersion())) {
            throw new BadRequestException("Event must have a version number.");
        }
        if (!StringUtils.hasText(command.event().getEventContent())) {
            throw new BadRequestException("Event content cannot be null or empty");
        }
    }
}

