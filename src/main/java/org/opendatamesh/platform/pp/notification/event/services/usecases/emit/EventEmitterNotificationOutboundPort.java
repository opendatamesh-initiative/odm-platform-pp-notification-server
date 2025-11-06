package org.opendatamesh.platform.pp.notification.event.services.usecases.emit;

import org.opendatamesh.platform.pp.notification.notification.entities.Notification;

interface EventEmitterNotificationOutboundPort {
    Notification create(Notification notification);
    
    Notification update(Notification notification);
    
    Notification findById(Long sequenceId);
}
