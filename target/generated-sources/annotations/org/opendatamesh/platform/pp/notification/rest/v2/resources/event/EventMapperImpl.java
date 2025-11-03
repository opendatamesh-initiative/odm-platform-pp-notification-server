package org.opendatamesh.platform.pp.notification.rest.v2.resources.event;

import javax.annotation.processing.Generated;
import org.opendatamesh.platform.pp.notification.event.entities.Event;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-11-03T15:17:26+0100",
    comments = "version: 1.6.2, compiler: javac, environment: Java 21.0.9 (Amazon.com Inc.)"
)
@Component
public class EventMapperImpl implements EventMapper {

    @Override
    public Event toEntity(EventRes resource) {
        if ( resource == null ) {
            return null;
        }

        Event event = new Event();

        event.setCreatedAt( resource.getCreatedAt() );
        event.setUpdatedAt( resource.getUpdatedAt() );
        event.setSequenceId( resource.getSequenceId() );
        event.setResourceType( resource.getResourceType() );
        event.setResourceIdentifier( resource.getResourceIdentifier() );
        event.setType( resource.getType() );
        event.setEventTypeVersion( resource.getEventTypeVersion() );
        event.setEventContent( resource.getEventContent() );

        return event;
    }

    @Override
    public EventRes toRes(Event entity) {
        if ( entity == null ) {
            return null;
        }

        EventRes eventRes = new EventRes();

        eventRes.setCreatedAt( entity.getCreatedAt() );
        eventRes.setUpdatedAt( entity.getUpdatedAt() );
        eventRes.setSequenceId( entity.getSequenceId() );
        eventRes.setResourceType( entity.getResourceType() );
        eventRes.setResourceIdentifier( entity.getResourceIdentifier() );
        eventRes.setType( entity.getType() );
        eventRes.setEventTypeVersion( entity.getEventTypeVersion() );
        eventRes.setEventContent( entity.getEventContent() );

        return eventRes;
    }
}
