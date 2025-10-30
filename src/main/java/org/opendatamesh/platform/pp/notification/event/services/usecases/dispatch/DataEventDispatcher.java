package org.opendatamesh.platform.pp.notification.event.services.usecases.dispatch;

public interface DataEventDispatcher {
    void execute(DataEventDispatchCommand command, DataEventDispatcherPresenter presenter);
}


