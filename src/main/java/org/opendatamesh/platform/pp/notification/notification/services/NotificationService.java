package org.opendatamesh.platform.pp.notification.notification.services;

import org.opendatamesh.platform.pp.notification.notification.entities.Notification;
import org.opendatamesh.platform.pp.notification.rest.v2.resources.notification.NotificationRes;
import org.opendatamesh.platform.pp.notification.rest.v2.resources.notification.NotificationSearchOptions;
import org.opendatamesh.platform.pp.notification.utils.services.GenericMappedAndFilteredCrudService;

public interface NotificationService extends GenericMappedAndFilteredCrudService<NotificationSearchOptions, NotificationRes, Notification, Long> {
}
