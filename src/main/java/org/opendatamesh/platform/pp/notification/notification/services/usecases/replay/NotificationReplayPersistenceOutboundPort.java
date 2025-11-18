package org.opendatamesh.platform.pp.notification.notification.services.usecases.replay;

import org.opendatamesh.platform.pp.notification.notification.entities.Notification;

interface NotificationReplayPersistenceOutboundPort {
    Notification findById(Long sequenceId);
    
    Notification update(Notification notification);
}

