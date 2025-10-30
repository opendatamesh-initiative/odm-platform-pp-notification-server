package org.opendatamesh.platform.pp.notification.rest.v2.resources.notification;

import org.mapstruct.Mapper;
import org.opendatamesh.platform.pp.notification.notification.entities.Notification;

@Mapper(componentModel = "spring")
public interface NotificationMapper {

    NotificationRes toRes(Notification notification);

    Notification toEntity(NotificationRes notificationRes);
}
