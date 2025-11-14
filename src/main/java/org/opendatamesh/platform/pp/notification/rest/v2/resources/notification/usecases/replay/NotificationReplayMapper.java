package org.opendatamesh.platform.pp.notification.rest.v2.resources.notification.usecases.replay;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.opendatamesh.platform.pp.notification.notification.entities.Notification;
import org.opendatamesh.platform.pp.notification.rest.v2.resources.event.EventMapper;
import org.opendatamesh.platform.pp.notification.rest.v2.resources.notification.NotificationMapper;
import org.opendatamesh.platform.pp.notification.rest.v2.resources.subscription.SubscriptionMapper;

@Mapper(componentModel = "spring", uses = {NotificationMapper.class, EventMapper.class, SubscriptionMapper.class})
public interface NotificationReplayMapper {

    @Mapping(source = "sequenceId", target = "notification.sequenceId")
    @Mapping(source = "status", target = "notification.status")
    @Mapping(source = "event", target = "notification.event")
    @Mapping(source = "subscription", target = "notification.subscription")
    @Mapping(source = "errorMessage", target = "notification.errorMessage")
    @Mapping(source = "createdAt", target = "notification.createdAt")
    @Mapping(source = "updatedAt", target = "notification.updatedAt")
    NotificationReplayResponseRes toRes(Notification notification);
}

