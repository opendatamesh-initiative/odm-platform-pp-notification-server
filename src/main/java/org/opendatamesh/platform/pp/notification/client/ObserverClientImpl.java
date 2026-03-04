package org.opendatamesh.platform.pp.notification.client;

import org.opendatamesh.platform.pp.notification.notification.entities.Notification;
import org.opendatamesh.platform.pp.notification.rest.v1.controllers.EventNotificationV1Mapper;
import org.opendatamesh.platform.pp.notification.rest.v2.resources.client.observer.NotificationDispatchMapper;
import org.opendatamesh.platform.pp.notification.subscription.entities.Subscription;
import org.opendatamesh.platform.pp.notification.subscription.entities.SubscriptionSupportedApiVersion;
import org.opendatamesh.platform.pp.notification.utils.client.RestUtils;
import org.opendatamesh.platform.pp.notification.utils.client.RestUtilsFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class ObserverClientImpl implements ObserverClient {

    private static final Logger log = LoggerFactory.getLogger(ObserverClientImpl.class);

    /** Matches optional leading 'v'/'V' then captures the major version (first digit sequence). */
    private static final Pattern EVENT_TYPE_VERSION_MAJOR = Pattern.compile("(?i)^v?(\\d+)");

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


    /*
    *    Event Type Version       Observer Api Version
    *    v1.x.x   ------------->  V1  [Allowed]
    *    v1.x.x   ------------->  V2  [Allowed]
    *    v2.x.x   ------------->  V1  [NOT Allowed]
    *    v2.x.x   ------------->  V2  [Allowed]
    */
    @Override
    public void dispatchNotification(Notification notification) {
        Subscription subscription = notification.getSubscription();
        SubscriptionSupportedApiVersion apiVersion = SubscriptionSupportedApiVersion.valueOf(subscription.getObserverApiVersion().toUpperCase());

        if (isV2EventToV1Observer(notification, apiVersion)) {
            log.info("Skipping dispatch: v2 event type is not allowed for observer API V1 (subscription: {}).", subscription.getUuid());
            return;
        }

        String fullUrl = buildUrl(subscription.getObserverBaseUrl(), apiVersion);
        Object payload = buildNotificationRes(notification, apiVersion);
        restUtils.genericPost(
                fullUrl,
                new ArrayList<>(),
                payload,
                Object.class
        );
    }

    private boolean isV2EventToV1Observer(Notification notification, SubscriptionSupportedApiVersion apiVersion) {
        if (apiVersion != SubscriptionSupportedApiVersion.V1) {
            return false;
        }
        if (notification.getEvent() == null || notification.getEvent().getEventTypeVersion() == null) {
            return false;
        }
        String version = notification.getEvent().getEventTypeVersion().trim();
        if (version.isEmpty()) {
            return false;
        }
        Matcher matcher = EVENT_TYPE_VERSION_MAJOR.matcher(version);
        if (!matcher.find()) {
            return false;
        }
        return "2".equals(matcher.group(1));
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
