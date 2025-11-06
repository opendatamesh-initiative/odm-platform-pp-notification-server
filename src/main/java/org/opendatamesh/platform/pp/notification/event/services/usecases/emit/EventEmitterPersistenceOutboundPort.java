package org.opendatamesh.platform.pp.notification.event.services.usecases.emit;

import org.opendatamesh.platform.pp.notification.event.entities.Event;

interface EventEmitterPersistenceOutboundPort {
    Event create(Event event);
}
