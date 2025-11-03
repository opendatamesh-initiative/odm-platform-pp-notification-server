package org.opendatamesh.platform.pp.notification.subscription.entities;

import jakarta.annotation.Generated;
import jakarta.persistence.metamodel.EntityType;
import jakarta.persistence.metamodel.SingularAttribute;
import jakarta.persistence.metamodel.StaticMetamodel;

@StaticMetamodel(SubscriptionEventType.class)
@Generated("org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
public abstract class SubscriptionEventType_ {

	
	/**
	 * @see org.opendatamesh.platform.pp.notification.subscription.entities.SubscriptionEventType#eventName
	 **/
	public static volatile SingularAttribute<SubscriptionEventType, String> eventName;
	
	/**
	 * @see org.opendatamesh.platform.pp.notification.subscription.entities.SubscriptionEventType#subscriptionUuid
	 **/
	public static volatile SingularAttribute<SubscriptionEventType, String> subscriptionUuid;
	
	/**
	 * @see org.opendatamesh.platform.pp.notification.subscription.entities.SubscriptionEventType#subscription
	 **/
	public static volatile SingularAttribute<SubscriptionEventType, Subscription> subscription;
	
	/**
	 * @see org.opendatamesh.platform.pp.notification.subscription.entities.SubscriptionEventType
	 **/
	public static volatile EntityType<SubscriptionEventType> class_;
	
	/**
	 * @see org.opendatamesh.platform.pp.notification.subscription.entities.SubscriptionEventType#sequenceId
	 **/
	public static volatile SingularAttribute<SubscriptionEventType, Long> sequenceId;

	public static final String EVENT_NAME = "eventName";
	public static final String SUBSCRIPTION_UUID = "subscriptionUuid";
	public static final String SUBSCRIPTION = "subscription";
	public static final String SEQUENCE_ID = "sequenceId";

}

