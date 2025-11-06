package org.opendatamesh.platform.pp.notification.rest.v2.resources.event.usecases.emit;

import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "EventEmitCommandRes", description = "Command resource for emitting a new event to the notification system")
public class EventEmitCommandRes {
    @Schema(description = "Event details to be emitted", required = true)
    private Event event;

    public EventEmitCommandRes(Event event) {
        this.event = event;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    @Schema(name = "Event", description = "Event details to be emitted")
    public static class Event {

        @Schema(description = "Type of the resource that generated the event", example = "DATA_PRODUCT", required = true)
        private String resourceType;

        @Schema(description = "Identifier of the resource that generated the event", example = "d5b5b9ac-6a73-4c73-b9ce-4bfc10a1dba0", required = true)
        private String resourceIdentifier;

        @Schema(description = "Type of event emitted", example = "DATA_PRODUCT_CREATED", required = true)
        private String type;

        @Schema(description = "Version of the event type definition", example = "1.0.0", required = true)
        private String eventTypeVersion;

        @Schema(description = "JSON content of the event payload", required = true)
        private JsonNode eventContent;

        public Event() {
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

        public JsonNode getEventContent() {
            return eventContent;
        }

        public void setEventContent(JsonNode eventContent) {
            this.eventContent = eventContent;
        }
    }
}
