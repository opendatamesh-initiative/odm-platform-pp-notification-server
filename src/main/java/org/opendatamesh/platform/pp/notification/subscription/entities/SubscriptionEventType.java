package org.opendatamesh.platform.pp.notification.subscription.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "subscriptions_events_types")
public class SubscriptionEventType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long sequenceId;

    @ManyToOne
    @JoinColumn(name = "subscription_uuid", insertable = false, updatable = false)
    private Subscription subscription;

    @Column(name = "subscription_uuid")
    private String subscriptionUuid;

    @Column(name = "event_name")
    private String eventName;

    public Long getSequenceId() {
        return sequenceId;
    }

    public void setSequenceId(Long sequenceId) {
        this.sequenceId = sequenceId;
    }

    public String getSubscriptionUuid() {
        return subscriptionUuid;
    }

    public void setSubscriptionUuid(String subscriptionUuid) {
        this.subscriptionUuid = subscriptionUuid;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public Subscription getSubscription() {
        return subscription;
    }

    public void setSubscription(Subscription subscription) {
        this.subscription = subscription;
    }
}
