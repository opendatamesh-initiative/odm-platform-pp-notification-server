package org.opendatamesh.platform.pp.notification.event.entities;

import jakarta.persistence.*;
import org.opendatamesh.platform.pp.notification.utils.entities.VersionedEntity;

@Entity
@Table(name = "events")
public class Event extends VersionedEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long sequenceId;

    @Column(name = "resource_type")
    private String resourceType;

    @Column(name = "resource_identifier")
    private String resourceIdentifier;

    @Column(name = "event_type")
    private String type;

    @Column(name = "event_type_version")
    private String eventTypeVersion;

    @Column(name = "event_content")
    private String eventContent;

    public Long getSequenceId() {
        return sequenceId;
    }

    public void setSequenceId(Long sequenceId) {
        this.sequenceId = sequenceId;
    }

    public String getResourceType() {
        return resourceType;
    }

    public void setResourceType(String resourceType) {
        this.resourceType = resourceType;
    }

    public String getResourceIdentifier() {
        return resourceIdentifier;
    }

    public void setResourceIdentifier(String resourceIdentifier) {
        this.resourceIdentifier = resourceIdentifier;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getEventTypeVersion() {
        return eventTypeVersion;
    }

    public void setEventTypeVersion(String eventTypeVersion) {
        this.eventTypeVersion = eventTypeVersion;
    }

    public String getEventContent() {
        return eventContent;
    }

    public void setEventContent(String eventContent) {
        this.eventContent = eventContent;
    }
}
