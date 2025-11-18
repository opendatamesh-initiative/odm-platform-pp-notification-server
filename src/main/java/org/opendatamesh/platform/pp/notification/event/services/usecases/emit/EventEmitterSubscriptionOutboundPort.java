package org.opendatamesh.platform.pp.notification.event.services.usecases.emit;

import org.opendatamesh.platform.pp.notification.subscription.entities.Subscription;

import java.util.List;

interface EventEmitterSubscriptionOutboundPort {

    List<Subscription> findGenericSubscription();

    List<Subscription> findSubscriptionByEventType(String type);
}
