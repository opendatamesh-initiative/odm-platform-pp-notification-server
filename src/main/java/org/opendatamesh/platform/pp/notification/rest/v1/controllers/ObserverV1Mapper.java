package org.opendatamesh.platform.pp.notification.rest.v1.controllers;

import org.opendatamesh.platform.pp.notification.rest.v1.resources.ObserverResV1;
import org.opendatamesh.platform.pp.notification.rest.v2.resources.subscription.SubscriptionRes;
import org.opendatamesh.platform.pp.notification.rest.v2.resources.subscription.usecases.subscribe.SubscribeCommandRes;
import org.opendatamesh.platform.pp.notification.rest.v2.resources.subscription.usecases.subscribe.SubscribeResponseRes;
import org.springframework.stereotype.Component;

import java.sql.Date;

@Component
public class ObserverV1Mapper {

    public ObserverResV1 toObserverResV1(SubscriptionRes subscriptionRes, Long id) {
        ObserverResV1 observerResV1 = new ObserverResV1();
        observerResV1.setId(id);
        observerResV1.setName(subscriptionRes.getName());
        observerResV1.setDisplayName(subscriptionRes.getDisplayName());
        observerResV1.setObserverServerBaseUrl(subscriptionRes.getObserverServerBaseUrl());
        if (subscriptionRes.getCreatedAt() != null) {
            observerResV1.setCreatedAt(new Date(subscriptionRes.getCreatedAt().getTime()));
        }
        if (subscriptionRes.getUpdatedAt() != null) {
            observerResV1.setUpdatedAt(new Date(subscriptionRes.getUpdatedAt().getTime()));
        }
        return observerResV1;
    }

    public SubscriptionRes toSubscriptionRes(ObserverResV1 observerResV1) {
        SubscriptionRes subscriptionRes = new SubscriptionRes();
        subscriptionRes.setName(observerResV1.getName());
        subscriptionRes.setDisplayName(observerResV1.getDisplayName());
        subscriptionRes.setObserverServerBaseUrl(observerResV1.getObserverServerBaseUrl());
        // Set default API version to V1 for V1 observers
        subscriptionRes.setObserverApiVersion("V1");
        return subscriptionRes;
    }

    public SubscribeCommandRes toSubscribeCommandRes(ObserverResV1 observerResV1) {
        SubscribeCommandRes subscriptionCommandRes = new SubscribeCommandRes();
        subscriptionCommandRes.setObserverName(observerResV1.getName());
        subscriptionCommandRes.setObserverDisplayName(observerResV1.getDisplayName());
        subscriptionCommandRes.setObserverBaseUrl(observerResV1.getObserverServerBaseUrl());
        subscriptionCommandRes.setObserverApiVersion("V1");
        return subscriptionCommandRes;
    }

    public ObserverResV1 toObserverResV1(SubscribeResponseRes subscribeResponseRes, Long id) {
        ObserverResV1 observerResV1 = new ObserverResV1();
        observerResV1.setId(id);
        observerResV1.setName(subscribeResponseRes.getSubscription().getObserverName());
        observerResV1.setDisplayName(subscribeResponseRes.getSubscription().getObserverDisplayName());
        observerResV1.setObserverServerBaseUrl(subscribeResponseRes.getSubscription().getObserverBaseUrl());
        return observerResV1;
    }

}

