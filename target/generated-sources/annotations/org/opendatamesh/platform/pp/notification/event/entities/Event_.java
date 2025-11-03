package org.opendatamesh.platform.pp.notification.event.entities;

import jakarta.annotation.Generated;
import jakarta.persistence.metamodel.EntityType;
import jakarta.persistence.metamodel.SingularAttribute;
import jakarta.persistence.metamodel.StaticMetamodel;
import org.opendatamesh.platform.pp.notification.rest.v2.resources.event.EventType;

@StaticMetamodel(Event.class)
@Generated("org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
public abstract class Event_ extends org.opendatamesh.platform.pp.notification.utils.entities.VersionedEntity_ {

	
	/**
	 * @see org.opendatamesh.platform.pp.notification.event.entities.Event#resourceIdentifier
	 **/
	public static volatile SingularAttribute<Event, String> resourceIdentifier;
	
	/**
	 * @see org.opendatamesh.platform.pp.notification.event.entities.Event#eventTypeVersion
	 **/
	public static volatile SingularAttribute<Event, String> eventTypeVersion;
	
	/**
	 * @see org.opendatamesh.platform.pp.notification.event.entities.Event#eventContent
	 **/
	public static volatile SingularAttribute<Event, String> eventContent;
	
	/**
	 * @see org.opendatamesh.platform.pp.notification.event.entities.Event#type
	 **/
	public static volatile SingularAttribute<Event, EventType> type;
	
	/**
	 * @see org.opendatamesh.platform.pp.notification.event.entities.Event
	 **/
	public static volatile EntityType<Event> class_;
	
	/**
	 * @see org.opendatamesh.platform.pp.notification.event.entities.Event#sequenceId
	 **/
	public static volatile SingularAttribute<Event, Long> sequenceId;
	
	/**
	 * @see org.opendatamesh.platform.pp.notification.event.entities.Event#resourceType
	 **/
	public static volatile SingularAttribute<Event, String> resourceType;

	public static final String RESOURCE_IDENTIFIER = "resourceIdentifier";
	public static final String EVENT_TYPE_VERSION = "eventTypeVersion";
	public static final String EVENT_CONTENT = "eventContent";
	public static final String TYPE = "type";
	public static final String SEQUENCE_ID = "sequenceId";
	public static final String RESOURCE_TYPE = "resourceType";

}

