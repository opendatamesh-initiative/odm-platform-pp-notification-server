package org.opendatamesh.platform.pp.notification.event.services.usecases.dispatch;

import org.opendatamesh.platform.pp.notification.event.entities.Event;
import org.opendatamesh.platform.pp.notification.event.repositories.EventRepository;
import org.opendatamesh.platform.pp.notification.event.services.EventService;
import org.opendatamesh.platform.pp.notification.rest.v2.resources.event.EventRes;
import org.springframework.stereotype.Component;

@Component
public class DataEventDispatcherPersistenceOutboundPortImpl implements DataEventDispatcherPersistenceOutboundPort {

    private final EventService eventService;
    private final EventRepository eventRepository;

    public DataEventDispatcherPersistenceOutboundPortImpl(EventService eventService,
                                                          EventRepository eventRepository) {
        this.eventService = eventService;
        this.eventRepository = eventRepository;
    }

    @Override
    public Event saveEvent(Event event) {
        EventRes res = new EventRes();
        res.setType(event.getType());
        res.setResourceIdentifier(event.getResourceIdentifier());
        res.setResourceType(event.getResourceType());
        res.setEventContent(event.getEventContent());
        EventRes created = eventService.createResource(res);
        // Load back entity to get all fields populated
        return eventRepository.findById(created.getSequenceId()).orElseGet(() -> {
            Event e = new Event();
            e.setSequenceId(created.getSequenceId());
            return e;
        });
    }
}


