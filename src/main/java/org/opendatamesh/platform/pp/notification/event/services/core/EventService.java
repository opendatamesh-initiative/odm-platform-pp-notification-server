package org.opendatamesh.platform.pp.notification.event.services.core;

import org.opendatamesh.platform.pp.notification.event.entities.Event;
import org.opendatamesh.platform.pp.notification.rest.v2.resources.event.EventRes;
import org.opendatamesh.platform.pp.notification.rest.v2.resources.event.EventSearchOptions;
import org.opendatamesh.platform.pp.notification.utils.services.GenericMappedAndFilteredCrudService;

public interface EventService extends GenericMappedAndFilteredCrudService<EventSearchOptions, EventRes, Event, Long> {


}
