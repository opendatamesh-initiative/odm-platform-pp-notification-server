package org.opendatamesh.platform.pp.notification.rest.v1.resources;


import java.sql.Date;

public class EventNotificationResV1 {
    private Long id;
    private EventResV1 event;
    private EventNotificationStatusResV1 status;
    private String processingOutput = "";
    private ObserverResV1 observer;
    private Date receivedAt;
    private Date processedAt;

    public EventNotificationResV1() {
    }

    public EventNotificationResV1(EventNotificationResV1 other) {
        if (other == null) return;

        this.id = other.id;
        this.status = other.status;
        this.processingOutput = other.processingOutput;

        this.receivedAt = (other.receivedAt != null) ? new Date(other.receivedAt.getTime()) : null;
        this.processedAt = (other.processedAt != null) ? new Date(other.processedAt.getTime()) : null;

        this.event = (other.event != null) ? new EventResV1(other.event) : null;
        this.observer = (other.observer != null) ? new ObserverResV1(other.observer) : null;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public EventResV1 getEvent() {
        return event;
    }

    public void setEvent(EventResV1 event) {
        this.event = event;
    }

    public EventNotificationStatusResV1 getStatus() {
        return status;
    }

    public void setStatus(EventNotificationStatusResV1 status) {
        this.status = status;
    }

    public String getProcessingOutput() {
        return processingOutput;
    }

    public void setProcessingOutput(String processingOutput) {
        this.processingOutput = processingOutput;
    }

    public ObserverResV1 getObserver() {
        return observer;
    }

    public void setObserver(ObserverResV1 observer) {
        this.observer = observer;
    }

    public Date getReceivedAt() {
        return receivedAt;
    }

    public void setReceivedAt(Date receivedAt) {
        this.receivedAt = receivedAt;
    }

    public Date getProcessedAt() {
        return processedAt;
    }

    public void setProcessedAt(Date processedAt) {
        this.processedAt = processedAt;
    }

    @Override
    public String toString() {
        return "OdmEventNotificationResource{" +
                "id=" + id +
                ", event=" + event +
                ", status=" + status +
                ", processingOutput='" + processingOutput + '\'' +
                ", observer=" + observer +
                ", receivedAt=" + receivedAt +
                ", processedAt=" + processedAt +
                '}';
    }
}
