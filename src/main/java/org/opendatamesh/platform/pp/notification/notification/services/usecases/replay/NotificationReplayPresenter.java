package org.opendatamesh.platform.pp.notification.notification.services.usecases.replay;

public interface NotificationReplayPresenter {
    void success(Long replayedNotificationId);
    void failure(Throwable error);
}



