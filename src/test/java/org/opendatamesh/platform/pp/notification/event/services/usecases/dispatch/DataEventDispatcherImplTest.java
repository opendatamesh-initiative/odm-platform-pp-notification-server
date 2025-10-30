package org.opendatamesh.platform.pp.notification.event.services.usecases.dispatch;

import org.junit.jupiter.api.Test;
import org.opendatamesh.platform.pp.notification.event.entities.Event;
import org.opendatamesh.platform.pp.notification.notification.entities.Notification;
import org.opendatamesh.platform.pp.notification.rest.v2.resources.event.EventType;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DataEventDispatcherImplTest {

    @Test
    void dispatchesAndReturnsSequenceId() {
        DataEventDispatcherPersistenceOutboundPort persistence = e -> {
            e.setSequenceId(42L);
            return e;
        };
        final int[] sent = {0};
        DataEventDispatcherNotificationOutboundPort notify = n -> sent[0]++;

        DataEventDispatcherImpl dispatcher = new DataEventDispatcherImpl(persistence, notify);
        final long[] result = {0};
        dispatcher.execute(new DataEventDispatchCommand(
                EventType.DATAPRODUCT_CREATED,
                "DATA_PRODUCT",
                "uuid-1",
                "{ }",
                "1.0"
        ), new DataEventDispatcherPresenter() {
            @Override
            public void success(long eventSequenceId) {
                result[0] = eventSequenceId;
            }

            @Override
            public void failure(Throwable error) { }
        });

        assertEquals(42L, result[0]);
        assertEquals(1, sent[0]);
    }
}


