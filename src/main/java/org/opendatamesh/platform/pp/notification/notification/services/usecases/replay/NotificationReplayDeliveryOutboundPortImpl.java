package org.opendatamesh.platform.pp.notification.notification.services.usecases.replay;

import org.opendatamesh.platform.pp.notification.notification.services.NotificationClient;
import org.opendatamesh.platform.pp.notification.rest.v2.resources.notification.NotificationRes;
import org.opendatamesh.platform.pp.notification.rest.v2.resources.subscription.SubscriptionRes;
import org.springframework.stereotype.Component;

@Component
public class NotificationReplayDeliveryOutboundPortImpl implements NotificationReplayDeliveryOutboundPort {

    private final NotificationClient notificationClient;

    public NotificationReplayDeliveryOutboundPortImpl(NotificationClient notificationClient) {
        this.notificationClient = notificationClient;
    }

    @Override
    public void sendNotificationToSubscription(SubscriptionRes subscription, NotificationRes notification) {
        notificationClient.dispatchNotificationToSubscription(subscription, notification);
    }
}



