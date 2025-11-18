package org.opendatamesh.platform.pp.notification.event.services.usecases.emit;

import org.opendatamesh.platform.pp.notification.event.entities.Event;
import org.opendatamesh.platform.pp.notification.event.services.core.EventService;

class EventEmitterPersistenceOutboundPortImpl implements EventEmitterPersistenceOutboundPort {

    private final EventService eventService;

    EventEmitterPersistenceOutboundPortImpl(EventService eventService) {
        this.eventService = eventService;
    }

    @Override
    public Event create(Event event) {
        return eventService.create(event);
    }
}

