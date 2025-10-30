package org.opendatamesh.platform.pp.notification.utils.entities;

import jakarta.annotation.Generated;
import jakarta.persistence.metamodel.MappedSuperclassType;
import jakarta.persistence.metamodel.SingularAttribute;
import jakarta.persistence.metamodel.StaticMetamodel;
import java.sql.Timestamp;

@StaticMetamodel(VersionedEntity.class)
@Generated("org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
public abstract class VersionedEntity_ {

	
	/**
	 * @see org.opendatamesh.platform.pp.notification.utils.entities.VersionedEntity#createdAt
	 **/
	public static volatile SingularAttribute<VersionedEntity, Timestamp> createdAt;
	
	/**
	 * @see org.opendatamesh.platform.pp.notification.utils.entities.VersionedEntity
	 **/
	public static volatile MappedSuperclassType<VersionedEntity> class_;
	
	/**
	 * @see org.opendatamesh.platform.pp.notification.utils.entities.VersionedEntity#updatedAt
	 **/
	public static volatile SingularAttribute<VersionedEntity, Timestamp> updatedAt;

	public static final String CREATED_AT = "createdAt";
	public static final String UPDATED_AT = "updatedAt";

}

