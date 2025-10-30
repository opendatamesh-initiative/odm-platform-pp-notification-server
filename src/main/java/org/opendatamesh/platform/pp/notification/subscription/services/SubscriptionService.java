package org.opendatamesh.platform.pp.notification.subscription.services;

import org.opendatamesh.platform.pp.notification.rest.v2.resources.subscription.SubscriptionRes;
import org.opendatamesh.platform.pp.notification.rest.v2.resources.subscription.SubscriptionSearchOptions;
import org.opendatamesh.platform.pp.notification.subscription.entities.Subscription;
import org.opendatamesh.platform.pp.notification.utils.services.GenericMappedAndFilteredCrudService;

public interface SubscriptionService extends GenericMappedAndFilteredCrudService<SubscriptionSearchOptions, SubscriptionRes, Subscription, String> {

}
