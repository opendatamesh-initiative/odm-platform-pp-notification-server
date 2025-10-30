package org.opendatamesh.platform.pp.notification.rest.v2.resources.subscription;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.opendatamesh.platform.pp.notification.subscription.entities.Subscription;
import org.opendatamesh.platform.pp.notification.subscription.entities.SubscriptionEventType;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-11-03T14:48:53+0100",
    comments = "version: 1.6.2, compiler: Eclipse JDT (IDE) 3.44.0.v20251001-1143, environment: Java 21.0.8 (Eclipse Adoptium)"
)
@Component
public class SubscriptionMapperImpl implements SubscriptionMapper {

    @Override
    public SubscriptionRes toRes(Subscription subscription) {
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

    @Override
    public Subscription toEntity(SubscriptionRes subscriptionRes) {
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
}
