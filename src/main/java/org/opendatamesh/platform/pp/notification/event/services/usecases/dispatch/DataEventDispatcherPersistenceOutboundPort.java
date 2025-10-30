package org.opendatamesh.platform.pp.notification.event.services.usecases.dispatch;

import org.opendatamesh.platform.pp.notification.event.entities.Event;

public interface DataEventDispatcherPersistenceOutboundPort {
    Event saveEvent(Event event);
}


