package org.opendatamesh.platform.pp.notification.event.services;

import org.opendatamesh.platform.pp.notification.event.entities.Event;
import org.opendatamesh.platform.pp.notification.event.services.usecases.emit.EventEmitterCommand;
import org.opendatamesh.platform.pp.notification.event.services.usecases.emit.EventEmitterFactory;
import org.opendatamesh.platform.pp.notification.rest.v2.resources.event.usecases.emit.EventEmitCommandRes;
import org.opendatamesh.platform.pp.notification.rest.v2.resources.event.usecases.emit.EventEmitMapper;
import org.opendatamesh.platform.pp.notification.rest.v2.resources.event.usecases.emit.EventEmitResponseRes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicReference;

@Service
public class EventUtilsService {
    @Autowired
    private EventEmitterFactory eventEmitterFactory;
    
    @Autowired
    private EventEmitMapper mapper;

    public EventEmitResponseRes emit(EventEmitCommandRes emitCommandRes) {
        Event eventEntity = mapper.toEntity(emitCommandRes);
        AtomicReference<Event> createdEvent = new AtomicReference<>();
        eventEmitterFactory.buildEventEmitter(
                new EventEmitterCommand(eventEntity),
                createdEvent::set
        ).execute();
        return mapper.toRes(createdEvent.get());
    }
}
