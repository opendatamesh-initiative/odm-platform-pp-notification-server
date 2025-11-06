package org.opendatamesh.platform.pp.notification.event.services.usecases.emit;

import org.opendatamesh.platform.pp.notification.event.entities.Event;

public interface EventEmitterPresenter {

    void presentCreatedEvent(Event savedEvent);
}
