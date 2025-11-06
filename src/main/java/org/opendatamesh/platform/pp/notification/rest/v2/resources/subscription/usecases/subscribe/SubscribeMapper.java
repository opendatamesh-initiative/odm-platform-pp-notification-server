package org.opendatamesh.platform.pp.notification.rest.v2.resources.subscription.usecases.subscribe;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.opendatamesh.platform.pp.notification.subscription.entities.Subscription;
import org.opendatamesh.platform.pp.notification.subscription.entities.SubscriptionEventType;

import java.util.Collections;
import java.util.List;

@Mapper(componentModel = "spring")
public interface SubscribeMapper {

    @Mapping(source = "uuid", target = "subscription.uuid")
    @Mapping(source = "name", target = "subscription.observerName")
    @Mapping(source = "displayName", target = "subscription.observerDisplayName")
    @Mapping(source = "observerServerBaseUrl", target = "subscription.observerBaseUrl")
    @Mapping(source = "observerApiVersion", target = "subscription.observerApiVersion")
    @Mapping(source = "eventTypes", target = "subscription.eventTypes", qualifiedByName = "eventTypesToStringList")
    SubscribeResponseRes toRes(Subscription subscription);

    @Named("eventTypesToStringList")
    default List<String> eventTypesToStringList(List<SubscriptionEventType> eventTypes) {
        if (eventTypes == null) {
            return Collections.emptyList();
        }
        return eventTypes.stream()
                .map(SubscriptionEventType::getEventType)
                .toList();
    }
}

