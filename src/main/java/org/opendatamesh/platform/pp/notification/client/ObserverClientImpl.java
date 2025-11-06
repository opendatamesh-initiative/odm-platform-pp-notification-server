package org.opendatamesh.platform.pp.notification.client;

import org.opendatamesh.platform.pp.notification.notification.entities.Notification;
import org.opendatamesh.platform.pp.notification.rest.v1.controllers.EventNotificationV1Mapper;
import org.opendatamesh.platform.pp.notification.rest.v2.resources.client.observer.NotificationDispatchMapper;
import org.opendatamesh.platform.pp.notification.subscription.entities.Subscription;
import org.opendatamesh.platform.pp.notification.subscription.entities.SubscriptionSupportedApiVersion;
import org.opendatamesh.platform.pp.notification.utils.client.RestUtils;
import org.opendatamesh.platform.pp.notification.utils.client.RestUtilsFactory;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class ObserverClientImpl implements ObserverClient {

    private static final String OBSERVER_ENDPOINT_V2 = "/api/v2/up/observer/notifications";
    private static final String OBSERVER_ENDPOINT_V1 = "/api/v1/up/observer/notifications";
    private final RestUtils restUtils;
    private final EventNotificationV1Mapper eventNotificationV1Mapper;
    private final NotificationDispatchMapper notificationDispatchMapper;

    public ObserverClientImpl(RestTemplateBuilder restTemplateBuilder, EventNotificationV1Mapper eventNotificationV1Mapper, NotificationDispatchMapper notificationDispatchMapper) {
        this.restUtils = RestUtilsFactory.getRestUtils(restTemplateBuilder.build());
        this.eventNotificationV1Mapper = eventNotificationV1Mapper;
        this.notificationDispatchMapper = notificationDispatchMapper;
    }


    @Override
    public void dispatchNotification(Notification notification) {
        Subscription subscription = notification.getSubscription();
        SubscriptionSupportedApiVersion apiVersion = SubscriptionSupportedApiVersion.valueOf(notification.getSubscription().getObserverApiVersion().toUpperCase());
        String fullUrl = buildUrl(subscription.getObserverServerBaseUrl(), apiVersion);
        Object payload = buildNotificationRes(notification, apiVersion);
        restUtils.genericPost(
                fullUrl,
                new ArrayList<>(),
                payload,
                String.class
        );
    }

    private Object buildNotificationRes(Notification notification, SubscriptionSupportedApiVersion apiVersion) {
        return switch (apiVersion) {
            case V1 -> eventNotificationV1Mapper.toRes(notification);
            case V2 -> notificationDispatchMapper.toRes(notification);
        };
    }

    private String buildUrl(String baseUrl, SubscriptionSupportedApiVersion apiVersion) {
        return switch (apiVersion) {
            case V1 -> baseUrl + OBSERVER_ENDPOINT_V1;
            case V2 -> baseUrl + OBSERVER_ENDPOINT_V2;
        };
    }
}
