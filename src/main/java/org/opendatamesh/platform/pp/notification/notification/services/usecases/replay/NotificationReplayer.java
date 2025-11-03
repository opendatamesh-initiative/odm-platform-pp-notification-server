package org.opendatamesh.platform.pp.notification.notification.services.usecases.replay;

public interface NotificationReplayer {
    void execute(NotificationReplayCommand command, NotificationReplayPresenter presenter);
}



