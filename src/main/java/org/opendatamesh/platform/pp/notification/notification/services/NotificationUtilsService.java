package org.opendatamesh.platform.pp.notification.notification.services;

import org.opendatamesh.platform.pp.notification.notification.entities.Notification;
import org.opendatamesh.platform.pp.notification.notification.services.usecases.replay.NotificationReplayCommand;
import org.opendatamesh.platform.pp.notification.notification.services.usecases.replay.NotificationReplayFactory;
import org.opendatamesh.platform.pp.notification.rest.v2.resources.notification.usecases.replay.NotificationReplayCommandRes;
import org.opendatamesh.platform.pp.notification.rest.v2.resources.notification.usecases.replay.NotificationReplayMapper;
import org.opendatamesh.platform.pp.notification.rest.v2.resources.notification.usecases.replay.NotificationReplayResponseRes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicReference;

@Service
public class NotificationUtilsService {
    
    @Autowired
    private NotificationReplayFactory notificationReplayFactory;
    
    @Autowired
    private NotificationReplayMapper mapper;

    public NotificationReplayResponseRes replay(NotificationReplayCommandRes replayCommandRes) {
        NotificationReplayCommand command = new NotificationReplayCommand(replayCommandRes.getNotificationSequenceId());
        AtomicReference<Notification> replayedNotification = new AtomicReference<>();
        notificationReplayFactory.buildNotificationReplay(
                command,
                replayedNotification::set
        ).execute();
        return mapper.toRes(replayedNotification.get());
    }
}

