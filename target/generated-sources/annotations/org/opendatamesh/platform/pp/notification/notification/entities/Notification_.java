package org.opendatamesh.platform.pp.notification.notification.entities;

import jakarta.annotation.Generated;
import jakarta.persistence.metamodel.EntityType;
import jakarta.persistence.metamodel.SingularAttribute;
import jakarta.persistence.metamodel.StaticMetamodel;
import org.opendatamesh.platform.pp.notification.event.entities.Event;
import org.opendatamesh.platform.pp.notification.rest.v2.resources.notification.NotificationStatus;
import org.opendatamesh.platform.pp.notification.subscription.entities.Subscription;

@StaticMetamodel(Notification.class)
@Generated("org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
public abstract class Notification_ {

	
	/**
	 * @see org.opendatamesh.platform.pp.notification.notification.entities.Notification#errorMessage
	 **/
	public static volatile SingularAttribute<Notification, String> errorMessage;
	
	/**
	 * @see org.opendatamesh.platform.pp.notification.notification.entities.Notification#subscription
	 **/
	public static volatile SingularAttribute<Notification, Subscription> subscription;
	
	/**
	 * @see org.opendatamesh.platform.pp.notification.notification.entities.Notification#event
	 **/
	public static volatile SingularAttribute<Notification, Event> event;
	
	/**
	 * @see org.opendatamesh.platform.pp.notification.notification.entities.Notification
	 **/
	public static volatile EntityType<Notification> class_;
	
	/**
	 * @see org.opendatamesh.platform.pp.notification.notification.entities.Notification#sequenceId
	 **/
	public static volatile SingularAttribute<Notification, Long> sequenceId;
	
	/**
	 * @see org.opendatamesh.platform.pp.notification.notification.entities.Notification#status
	 **/
	public static volatile SingularAttribute<Notification, NotificationStatus> status;

	public static final String ERROR_MESSAGE = "errorMessage";
	public static final String SUBSCRIPTION = "subscription";
	public static final String EVENT = "event";
	public static final String SEQUENCE_ID = "sequenceId";
	public static final String STATUS = "status";

}

