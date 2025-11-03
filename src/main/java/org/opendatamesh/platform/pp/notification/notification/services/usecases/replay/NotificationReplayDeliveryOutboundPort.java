package org.opendatamesh.platform.pp.notification.notification.services.usecases.replay;

import org.opendatamesh.platform.pp.notification.rest.v2.resources.notification.NotificationRes;
import org.opendatamesh.platform.pp.notification.rest.v2.resources.subscription.SubscriptionRes;

public interface NotificationReplayDeliveryOutboundPort {
    void sendNotificationToSubscription(SubscriptionRes subscription, NotificationRes notification);
}


