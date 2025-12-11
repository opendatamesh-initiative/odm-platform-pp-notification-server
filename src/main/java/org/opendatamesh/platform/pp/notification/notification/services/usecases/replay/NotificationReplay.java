package org.opendatamesh.platform.pp.notification.notification.services.usecases.replay;

import org.opendatamesh.platform.pp.notification.exceptions.BadRequestException;
import org.opendatamesh.platform.pp.notification.notification.entities.Notification;
import org.opendatamesh.platform.pp.notification.notification.entities.NotificationStatus;
import org.opendatamesh.platform.pp.notification.utils.services.programmatic.EntityInitAndDetachService;
import org.opendatamesh.platform.pp.notification.utils.usecases.TransactionalOutboundPort;
import org.opendatamesh.platform.pp.notification.utils.usecases.UseCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Use case for replaying a failed notification
 * <p>
 * This use case handles the replay of a notification that previously failed to deliver.
 * It retrieves the notification, resets its status to PROCESSING, and dispatches it again to the observer.
 * </p>
 */
class NotificationReplay implements UseCase {

    private static final String USE_CASE_PREFIX = "[NotificationReplay]";
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final NotificationReplayCommand command;
    private final NotificationReplayPresenter presenter;

    private final TransactionalOutboundPort transactionalPort;
    private final EntityInitAndDetachService entityDetacher;

    private final NotificationReplayPersistenceOutboundPort persistencePort;
    private final NotificationReplayObserverOutboundPort observerPort;

    NotificationReplay(
            NotificationReplayCommand command,
            NotificationReplayPresenter presenter,
            TransactionalOutboundPort transactionalPort,
            EntityInitAndDetachService entityDetacher,
            NotificationReplayPersistenceOutboundPort persistencePort,
            NotificationReplayObserverOutboundPort observerPort) {
        this.command = command;
        this.presenter = presenter;
        this.transactionalPort = transactionalPort;
        this.entityDetacher = entityDetacher;
        this.persistencePort = persistencePort;
        this.observerPort = observerPort;
    }

    @Override
    public void execute() {
        validateCommand();
        logger.info("{} Starting notification replay for notification sequence ID: {}", USE_CASE_PREFIX, command.notificationSequenceId());

        Notification notification = persistencePort.findById(command.notificationSequenceId());
        warnForIncorrectStatus(notification);

        Notification updatedNotification = resetStatusToProcessing(notification);

        try {
            observerPort.dispatchNotification(updatedNotification);
            logger.info("{} Successfully re-dispatched notification {} to observer: {} at {}",
                    USE_CASE_PREFIX, updatedNotification.getSequenceId(),
                    updatedNotification.getSubscription().getDisplayName(),
                    updatedNotification.getSubscription().getObserverBaseUrl());
            // Note: We don't update the notification here to avoid race condition with observer
            // Only update on failure to avoid conflicts with observer's status updates
            presenter.presentReplayedNotification(updatedNotification);
        } catch (Exception e) {
            // This is safe because observer won't update a notification that failed to be delivered
            logger.warn("{} Error when re-dispatching notification {} to observer: {}, error: {}",
                    USE_CASE_PREFIX, updatedNotification.getSequenceId(),
                    updatedNotification.getSubscription().getDisplayName(), e.getMessage(), e);
            handleNotificationFailure(e.getMessage(), updatedNotification);
        }

    }

    private Notification resetStatusToProcessing(Notification notification) {
        return transactionalPort.doInTransactionWithResults((Void v) -> {
            notification.setStatus(NotificationStatus.PROCESSING);
            notification.setErrorMessage(null);
            Notification savedNotification = persistencePort.update(notification);
            entityDetacher.initializeEntityAndDetach(savedNotification);
            return savedNotification;
        }, null);
    }

    private void warnForIncorrectStatus(Notification notification) {
        if (notification.getStatus() != NotificationStatus.FAILED_TO_DELIVER &&
                notification.getStatus() != NotificationStatus.FAILED_TO_PROCESS) {
            logger.warn("{} Notification with sequence ID {} is being replayed with status: {}. " +
                            "Expected status: FAILED_TO_DELIVER or FAILED_TO_PROCESS.",
                    USE_CASE_PREFIX, command.notificationSequenceId(), notification.getStatus());
        }
    }

    private void handleNotificationFailure(String errorMessage, Notification notification) {
        Notification updatedNotification = transactionalPort.doInTransactionWithResults((Notification n) -> {
            if (n != null && n.getStatus() == NotificationStatus.PROCESSING) {
                n.setStatus(NotificationStatus.FAILED_TO_DELIVER);
                n.setErrorMessage(errorMessage);
                return persistencePort.update(n);
            }
            return notification;
        }, notification);
        presenter.presentReplayedNotification(updatedNotification);
    }

    private void validateCommand() {
        if (command == null) {
            throw new BadRequestException("Command cannot be null");
        }
        if (command.notificationSequenceId() == null) {
            throw new BadRequestException("Notification sequence ID cannot be null");
        }
    }
}

