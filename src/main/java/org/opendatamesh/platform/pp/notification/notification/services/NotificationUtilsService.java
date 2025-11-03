package org.opendatamesh.platform.pp.notification.notification.services;

import org.opendatamesh.platform.pp.notification.exceptions.BadRequestException;
import org.opendatamesh.platform.pp.notification.exceptions.NotFoundException;
import org.opendatamesh.platform.pp.notification.notification.services.usecases.replay.NotificationReplayCommand;
import org.opendatamesh.platform.pp.notification.notification.services.usecases.replay.NotificationReplayPresenter;
import org.opendatamesh.platform.pp.notification.notification.services.usecases.replay.NotificationReplayer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Service for notification utility operations like replay
 */
@Service
public class NotificationUtilsService {

    private static final Logger log = LoggerFactory.getLogger(NotificationUtilsService.class);

    @Autowired
    private NotificationReplayer notificationReplayer;

    /**
     * Replay a notification to its original subscriber (non-broadcast)
     *
     * @param notificationId The ID of the notification to replay
     * @return The ID of the newly created replayed notification
     * @throws NotFoundException   if the notification doesn't exist
     * @throws BadRequestException if the notification is invalid or missing required data
     */
    public Long replay(Long notificationId) {
        log.info("Replaying notification with ID: {}", notificationId);
        if (notificationId == null) {
            throw new BadRequestException("Invalid notificatio id");
        }
        try {
            NotificationReplayCommand command = new NotificationReplayCommand(notificationId);
            NotificationReplayPresenterImpl presenter = new NotificationReplayPresenterImpl();

            notificationReplayer.execute(command, presenter);

            if (presenter.getError() != null) {
                handleError(presenter.getError());
            }

            Long replayedNotificationId = presenter.getReplayedNotificationId();
            log.info("Successfully replayed notification {} as new notification {}",
                    notificationId, replayedNotificationId);

            return replayedNotificationId;

        } catch (NotFoundException e) {
            throw new NotFoundException("Original Notification not found");
        } catch (Exception e) {
            log.error("Unexpected error replaying notification {}: {}", notificationId, e.getMessage(), e);
            throw new RuntimeException("Failed to replay notification: " + e.getMessage(), e);
        }
    }

    private void handleError(Throwable error) {
        if (error instanceof NotFoundException) {
            throw (NotFoundException) error;
        }
        if (error instanceof IllegalArgumentException) {
            throw new BadRequestException(error.getMessage());
        }
        // For any other error, wrap it in a RuntimeException
        throw new RuntimeException("Failed to replay notification: " + error.getMessage(), error);
    }

    private static class NotificationReplayPresenterImpl implements NotificationReplayPresenter {
        private Long replayedNotificationId;
        private Throwable error;

        @Override
        public void success(Long replayedNotificationId) {
            this.replayedNotificationId = replayedNotificationId;
            this.error = null;
        }

        @Override
        public void failure(Throwable error) {
            this.error = error;
            this.replayedNotificationId = null;
        }

        public Long getReplayedNotificationId() {
            return replayedNotificationId;
        }

        public Throwable getError() {
            return error;
        }
    }
}

