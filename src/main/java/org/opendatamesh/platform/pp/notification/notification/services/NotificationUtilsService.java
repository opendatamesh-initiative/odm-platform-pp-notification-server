package org.opendatamesh.platform.pp.notification.notification.services;

import org.opendatamesh.platform.pp.notification.exceptions.BadRequestException;
import org.opendatamesh.platform.pp.notification.exceptions.NotFoundException;
import org.opendatamesh.platform.pp.notification.notification.services.usecases.replay.NotificationReplayCommand;
import org.opendatamesh.platform.pp.notification.notification.services.usecases.replay.NotificationReplayPresenter;
import org.opendatamesh.platform.pp.notification.notification.services.usecases.replay.NotificationReplayer;
import org.opendatamesh.platform.pp.notification.rest.v2.resources.notification.NotificationRes;
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

    @Autowired
    private NotificationService notificationService;

    public NotificationRes replay(Long notificationId) {
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

            // Retrieve and return the complete notification
            NotificationRes replayedNotification = notificationService.findOneResource(replayedNotificationId);
            return replayedNotification;

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

