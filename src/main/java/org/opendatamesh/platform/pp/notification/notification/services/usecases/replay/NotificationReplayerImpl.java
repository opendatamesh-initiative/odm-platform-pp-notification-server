package org.opendatamesh.platform.pp.notification.notification.services.usecases.replay;

import org.opendatamesh.platform.pp.notification.exceptions.NotFoundException;
import org.opendatamesh.platform.pp.notification.rest.v2.resources.notification.NotificationRes;
import org.opendatamesh.platform.pp.notification.rest.v2.resources.notification.NotificationStatus;
import org.opendatamesh.platform.pp.notification.rest.v2.resources.subscription.SubscriptionRes;

public class NotificationReplayerImpl implements NotificationReplayer {

    private final NotificationReplayPersistenceOutboundPort persistencePort;
    private final NotificationReplayDeliveryOutboundPort deliveryPort;

    public NotificationReplayerImpl(
            NotificationReplayPersistenceOutboundPort persistencePort,
            NotificationReplayDeliveryOutboundPort deliveryPort
    ) {
        this.persistencePort = persistencePort;
        this.deliveryPort = deliveryPort;
    }

    @Override
    public void execute(NotificationReplayCommand command, NotificationReplayPresenter presenter) {
        try {
            // 1. Find the original notification
            NotificationRes originalNotification = persistencePort.findNotificationById(command.getNotificationId());
            if (originalNotification == null) {
                presenter.failure(new NotFoundException("Notification with ID " + command.getNotificationId() + " not found"));
                return;
            }

            // 2. Validate that notification has subscription and event
            if (originalNotification.getSubscription() == null) {
                presenter.failure(new IllegalArgumentException("Notification does not have an associated subscription"));
                return;
            }
            if (originalNotification.getEvent() == null) {
                presenter.failure(new IllegalArgumentException("Notification does not have an associated event"));
                return;
            }

            SubscriptionRes subscription = originalNotification.getSubscription();

            // 3. Create new notification for replay with DELIVERED status
            NotificationRes replayedNotification = new NotificationRes();
            replayedNotification.setStatus(NotificationStatus.DELIVERED);
            replayedNotification.setEvent(originalNotification.getEvent());
            replayedNotification.setSubscription(subscription);

            // 4. Save the new notification
            NotificationRes savedNotification = persistencePort.saveNotification(replayedNotification);

            // 5. Send notification to subscription (async, non-broadcast)
            try {
                deliveryPort.sendNotificationToSubscription(subscription, savedNotification);
                presenter.success(savedNotification.getSequenceId());
            } catch (Exception e) {
                // Update notification status to FAILED if delivery fails
                savedNotification.setStatus(NotificationStatus.FAILED);
                savedNotification.setErrorMessage(e.getMessage());
                persistencePort.saveNotification(savedNotification);
                presenter.failure(e);
            }
        } catch (Throwable t) {
            presenter.failure(t);
        }
    }
}

