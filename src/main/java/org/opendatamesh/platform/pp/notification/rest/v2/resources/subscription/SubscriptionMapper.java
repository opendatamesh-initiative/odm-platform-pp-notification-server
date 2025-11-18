package org.opendatamesh.platform.pp.notification.rest.v2.resources.subscription;

import org.mapstruct.Mapper;
import org.opendatamesh.platform.pp.notification.subscription.entities.Subscription;

@Mapper(componentModel = "spring")
public interface SubscriptionMapper {

    SubscriptionRes toRes(Subscription subscription);

    Subscription toEntity(SubscriptionRes subscriptionRes);
}
