package org.opendatamesh.platform.pp.notification.subscription.entities;

import jakarta.annotation.Generated;
import jakarta.persistence.metamodel.EntityType;
import jakarta.persistence.metamodel.ListAttribute;
import jakarta.persistence.metamodel.SingularAttribute;
import jakarta.persistence.metamodel.StaticMetamodel;

@StaticMetamodel(Subscription.class)
@Generated("org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
public abstract class Subscription_ extends org.opendatamesh.platform.pp.notification.utils.entities.VersionedEntity_ {

	
	/**
	 * @see org.opendatamesh.platform.pp.notification.subscription.entities.Subscription#displayName
	 **/
	public static volatile SingularAttribute<Subscription, String> displayName;
	
	/**
	 * @see org.opendatamesh.platform.pp.notification.subscription.entities.Subscription#observerServerBaseUrl
	 **/
	public static volatile SingularAttribute<Subscription, String> observerServerBaseUrl;
	
	/**
	 * @see org.opendatamesh.platform.pp.notification.subscription.entities.Subscription#name
	 **/
	public static volatile SingularAttribute<Subscription, String> name;
	
	/**
	 * @see org.opendatamesh.platform.pp.notification.subscription.entities.Subscription#eventTypes
	 **/
	public static volatile ListAttribute<Subscription, SubscriptionEventType> eventTypes;
	
	/**
	 * @see org.opendatamesh.platform.pp.notification.subscription.entities.Subscription
	 **/
	public static volatile EntityType<Subscription> class_;
	
	/**
	 * @see org.opendatamesh.platform.pp.notification.subscription.entities.Subscription#uuid
	 **/
	public static volatile SingularAttribute<Subscription, String> uuid;

	public static final String DISPLAY_NAME = "displayName";
	public static final String OBSERVER_SERVER_BASE_URL = "observerServerBaseUrl";
	public static final String NAME = "name";
	public static final String EVENT_TYPES = "eventTypes";
	public static final String UUID = "uuid";

}

