package org.opendatamesh.platform.pp.notification.rest.v2.resources.event;

import org.mapstruct.Mapper;
import org.opendatamesh.platform.pp.notification.event.entities.Event;

@Mapper(componentModel = "spring")
public interface EventMapper {

    Event toEntity(EventRes resource);

    EventRes toRes(Event entity);
}
