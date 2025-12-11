package org.opendatamesh.platform.pp.notification.subscription.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "subscriptions_events_types")
public class SubscriptionEventType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long sequenceId;

    @ManyToOne
    @JoinColumn(name = "subscription_uuid", referencedColumnName = "uuid")
    private Subscription subscription;

    @Column(name = "event_type")
    private String eventType;

    public SubscriptionEventType() {
    }

    public SubscriptionEventType(String eventType) {
        this.eventType = eventType;
    }

    public Long getSequenceId() {
        return sequenceId;
    }

    public void setSequenceId(Long sequenceId) {
        this.sequenceId = sequenceId;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public Subscription getSubscription() {
        return subscription;
    }

    public void setSubscription(Subscription subscription) {
        this.subscription = subscription;
    }
}
