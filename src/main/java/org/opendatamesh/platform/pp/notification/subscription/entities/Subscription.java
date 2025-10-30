package org.opendatamesh.platform.pp.notification.subscription.entities;

import jakarta.persistence.*;
import org.hibernate.annotations.GenericGenerator;
import org.opendatamesh.platform.pp.notification.utils.entities.VersionedEntity;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "subscriptions")
public class Subscription extends VersionedEntity {

    @Id
    @Column(name = "uuid")
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    private String uuid;

    @Column(name = "name")
    private String name;

    @Column(name = "display_name")
    private String displayName;

    @Column(name = "observer_server_base_url")
    private String observerServerBaseUrl;

    @OneToMany(mappedBy = "subscription", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SubscriptionEventType> eventTypes = new ArrayList<>();


    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getObserverServerBaseUrl() {
        return observerServerBaseUrl;
    }

    public void setObserverServerBaseUrl(String observerServerBaseUrl) {
        this.observerServerBaseUrl = observerServerBaseUrl;
    }

    public List<SubscriptionEventType> getEventTypes() {
        return eventTypes;
    }

    public void setEventTypes(List<SubscriptionEventType> eventTypes) {
        this.eventTypes = eventTypes;
    }
}
