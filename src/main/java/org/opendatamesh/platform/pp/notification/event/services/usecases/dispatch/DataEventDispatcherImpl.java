package org.opendatamesh.platform.pp.notification.event.services.usecases.dispatch;

import org.opendatamesh.platform.pp.notification.event.entities.Event;
import org.opendatamesh.platform.pp.notification.notification.entities.Notification;
import org.opendatamesh.platform.pp.notification.rest.v2.resources.notification.NotificationStatus;

public class DataEventDispatcherImpl implements DataEventDispatcher {

    private final DataEventDispatcherPersistenceOutboundPort persistencePort;
    private final DataEventDispatcherNotificationOutboundPort notificationPort;

    public DataEventDispatcherImpl(
            DataEventDispatcherPersistenceOutboundPort persistencePort,
            DataEventDispatcherNotificationOutboundPort notificationPort
    ) {
        this.persistencePort = persistencePort;
        this.notificationPort = notificationPort;
    }

    @Override
    public void execute(DataEventDispatchCommand command, DataEventDispatcherPresenter presenter) {
        try {
            Event event = new Event();
            event.setType(command.getEventType());
            event.setResourceType(command.getResourceType());
            event.setResourceIdentifier(command.getResourceIdentifier());
            event.setEventContent(command.getEventContent());
            event.setEventTypeVersion(command.getEventTypeVersion());

            Event saved = persistencePort.saveEvent(event);

            Notification notification = new Notification();
            notification.setStatus(NotificationStatus.DELIVERED);
            Event ref = new Event();
            ref.setSequenceId(saved.getSequenceId());
            notification.setEvent(ref);

            notificationPort.sendNotification(notification);

            presenter.success(saved.getSequenceId());
        } catch (Throwable t) {
            presenter.failure(t);
        }
    }
}


