package org.opendatamesh.platform.pp.notification.event.services.usecases.dispatch;

public interface DataEventDispatcherPresenter {
    void success(long eventSequenceId);

    void failure(Throwable error);
}


