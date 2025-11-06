package org.opendatamesh.platform.pp.notification.rest.v1.controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.opendatamesh.platform.pp.notification.rest.v1.resources.EventResV1;
import org.opendatamesh.platform.pp.notification.rest.v2.resources.event.usecases.emit.EventEmitCommandRes;
import org.springframework.stereotype.Component;

@Component
public class DispatchV1Mapper {

    private final ObjectMapper objectMapper;

    public DispatchV1Mapper() {
        this.objectMapper = new ObjectMapper();
    }

    public EventEmitCommandRes toEventEmitCommandRes(EventResV1 eventResV1) {
        EventEmitCommandRes.Event event = new EventEmitCommandRes.Event();
        
        // Map type
        event.setType(eventResV1.getType());
        
        // Map entityId to resourceIdentifier
        event.setResourceIdentifier(eventResV1.getEntityId());
        
        // V1 API doesn't provide resourceType, so we default to "DATA_PRODUCT"
        // This is a reasonable default since V1 events are typically about data products
        event.setResourceType("DATA_PRODUCT");
        
        // Default event type version to "1.0.0" for V1 events
        event.setEventTypeVersion("1.0.0");
        
        // Build eventContent from beforeState and afterState
        JsonNode eventContent = buildEventContent(eventResV1.getBeforeState(), eventResV1.getAfterState());
        event.setEventContent(eventContent);
        
        return new EventEmitCommandRes(event);
    }


    private JsonNode buildEventContent(JsonNode beforeState, JsonNode afterState) {
        try {
            // Create a JSON object with beforeState and afterState
            String jsonContent = "{}";
            if (beforeState != null || afterState != null) {
                StringBuilder jsonBuilder = new StringBuilder("{");
                if (beforeState != null) {
                    jsonBuilder.append("\"beforeState\":").append(beforeState.toString());
                }
                if (afterState != null) {
                    if (beforeState != null) {
                        jsonBuilder.append(",");
                    }
                    jsonBuilder.append("\"afterState\":").append(afterState.toString());
                }
                jsonBuilder.append("}");
                jsonContent = jsonBuilder.toString();
            }
            return objectMapper.readTree(jsonContent);
        } catch (Exception e) {
            throw new RuntimeException("Failed to build event content from beforeState and afterState", e);
        }
    }
}

