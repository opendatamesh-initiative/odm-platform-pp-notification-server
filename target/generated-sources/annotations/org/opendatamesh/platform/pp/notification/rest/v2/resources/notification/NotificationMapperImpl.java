package org.opendatamesh.platform.pp.notification.rest.v2.resources.notification;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.opendatamesh.platform.pp.notification.event.entities.Event;
import org.opendatamesh.platform.pp.notification.notification.entities.Notification;
import org.opendatamesh.platform.pp.notification.rest.v2.resources.event.EventRes;
import org.opendatamesh.platform.pp.notification.rest.v2.resources.subscription.SubscriptionEventTypeRes;
import org.opendatamesh.platform.pp.notification.rest.v2.resources.subscription.SubscriptionRes;
import org.opendatamesh.platform.pp.notification.subscription.entities.Subscription;
import org.opendatamesh.platform.pp.notification.subscription.entities.SubscriptionEventType;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-11-03T15:17:26+0100",
    comments = "version: 1.6.2, compiler: javac, environment: Java 21.0.9 (Amazon.com Inc.)"
)
@Component
public class NotificationMapperImpl implements NotificationMapper {

    @Override
    public NotificationRes toRes(Notification notification) {
        if ( notification == null ) {
            return null;
        }

        NotificationRes notificationRes = new NotificationRes();

        notificationRes.setSequenceId( notification.getSequenceId() );
        notificationRes.setStatus( notification.getStatus() );
        notificationRes.setEvent( eventToEventRes( notification.getEvent() ) );
        notificationRes.setSubscription( subscriptionToSubscriptionRes( notification.getSubscription() ) );
        notificationRes.setErrorMessage( notification.getErrorMessage() );

        return notificationRes;
    }

    @Override
    public Notification toEntity(NotificationRes notificationRes) {
        if ( notificationRes == null ) {
            return null;
        }

        Notification notification = new Notification();

        notification.setSequenceId( notificationRes.getSequenceId() );
        notification.setStatus( notificationRes.getStatus() );
        notification.setEvent( eventResToEvent( notificationRes.getEvent() ) );
        notification.setSubscription( subscriptionResToSubscription( notificationRes.getSubscription() ) );
        notification.setErrorMessage( notificationRes.getErrorMessage() );

        return notification;
    }

    protected EventRes eventToEventRes(Event event) {
        if ( event == null ) {
            return null;
        }

        EventRes eventRes = new EventRes();

        eventRes.setCreatedAt( event.getCreatedAt() );
        eventRes.setUpdatedAt( event.getUpdatedAt() );
        eventRes.setSequenceId( event.getSequenceId() );
        eventRes.setResourceType( event.getResourceType() );
        eventRes.setResourceIdentifier( event.getResourceIdentifier() );
        eventRes.setType( event.getType() );
        eventRes.setEventTypeVersion( event.getEventTypeVersion() );
        eventRes.setEventContent( event.getEventContent() );

        return eventRes;
    }

    protected SubscriptionEventTypeRes subscriptionEventTypeToSubscriptionEventTypeRes(SubscriptionEventType subscriptionEventType) {
        if ( subscriptionEventType == null ) {
            return null;
        }

        SubscriptionEventTypeRes subscriptionEventTypeRes = new SubscriptionEventTypeRes();

        subscriptionEventTypeRes.setSequenceId( subscriptionEventType.getSequenceId() );
        subscriptionEventTypeRes.setEventName( subscriptionEventType.getEventName() );

        return subscriptionEventTypeRes;
    }

    protected List<SubscriptionEventTypeRes> subscriptionEventTypeListToSubscriptionEventTypeResList(List<SubscriptionEventType> list) {
        if ( list == null ) {
            return null;
        }

        List<SubscriptionEventTypeRes> list1 = new ArrayList<SubscriptionEventTypeRes>( list.size() );
        for ( SubscriptionEventType subscriptionEventType : list ) {
            list1.add( subscriptionEventTypeToSubscriptionEventTypeRes( subscriptionEventType ) );
        }

        return list1;
    }

    protected SubscriptionRes subscriptionToSubscriptionRes(Subscription subscription) {
        if ( subscription == null ) {
            return null;
        }

        SubscriptionRes subscriptionRes = new SubscriptionRes();

        subscriptionRes.setCreatedAt( subscription.getCreatedAt() );
        subscriptionRes.setUpdatedAt( subscription.getUpdatedAt() );
        subscriptionRes.setUuid( subscription.getUuid() );
        subscriptionRes.setName( subscription.getName() );
        subscriptionRes.setDisplayName( subscription.getDisplayName() );
        subscriptionRes.setObserverServerBaseUrl( subscription.getObserverServerBaseUrl() );
        subscriptionRes.setEventTypes( subscriptionEventTypeListToSubscriptionEventTypeResList( subscription.getEventTypes() ) );

        return subscriptionRes;
    }

    protected Event eventResToEvent(EventRes eventRes) {
        if ( eventRes == null ) {
            return null;
        }

        Event event = new Event();

        event.setCreatedAt( eventRes.getCreatedAt() );
        event.setUpdatedAt( eventRes.getUpdatedAt() );
        event.setSequenceId( eventRes.getSequenceId() );
        event.setResourceType( eventRes.getResourceType() );
        event.setResourceIdentifier( eventRes.getResourceIdentifier() );
        event.setType( eventRes.getType() );
        event.setEventTypeVersion( eventRes.getEventTypeVersion() );
        event.setEventContent( eventRes.getEventContent() );

        return event;
    }

    protected SubscriptionEventType subscriptionEventTypeResToSubscriptionEventType(SubscriptionEventTypeRes subscriptionEventTypeRes) {
        if ( subscriptionEventTypeRes == null ) {
            return null;
        }

        SubscriptionEventType subscriptionEventType = new SubscriptionEventType();

        subscriptionEventType.setSequenceId( subscriptionEventTypeRes.getSequenceId() );
        subscriptionEventType.setEventName( subscriptionEventTypeRes.getEventName() );

        return subscriptionEventType;
    }

    protected List<SubscriptionEventType> subscriptionEventTypeResListToSubscriptionEventTypeList(List<SubscriptionEventTypeRes> list) {
        if ( list == null ) {
            return null;
        }

        List<SubscriptionEventType> list1 = new ArrayList<SubscriptionEventType>( list.size() );
        for ( SubscriptionEventTypeRes subscriptionEventTypeRes : list ) {
            list1.add( subscriptionEventTypeResToSubscriptionEventType( subscriptionEventTypeRes ) );
        }

        return list1;
    }

    protected Subscription subscriptionResToSubscription(SubscriptionRes subscriptionRes) {
        if ( subscriptionRes == null ) {
            return null;
        }

        Subscription subscription = new Subscription();

        subscription.setCreatedAt( subscriptionRes.getCreatedAt() );
        subscription.setUpdatedAt( subscriptionRes.getUpdatedAt() );
        subscription.setUuid( subscriptionRes.getUuid() );
        subscription.setName( subscriptionRes.getName() );
        subscription.setDisplayName( subscriptionRes.getDisplayName() );
        subscription.setObserverServerBaseUrl( subscriptionRes.getObserverServerBaseUrl() );
        subscription.setEventTypes( subscriptionEventTypeResListToSubscriptionEventTypeList( subscriptionRes.getEventTypes() ) );

        return subscription;
    }
}
