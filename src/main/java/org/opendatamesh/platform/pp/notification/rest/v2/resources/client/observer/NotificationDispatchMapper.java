package org.opendatamesh.platform.pp.notification.rest.v2.resources.client.observer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.opendatamesh.platform.pp.notification.notification.entities.Notification;

@Mapper(componentModel = "spring")
public interface NotificationDispatchMapper {
    
    @Mapping(source = "event.sequenceId", target = "event.sequenceId")
    @Mapping(source = "event.resourceType", target = "event.resourceType")
    @Mapping(source = "event.resourceIdentifier", target = "event.resourceIdentifier")
    @Mapping(source = "event.type", target = "event.type")
    @Mapping(source = "event.eventTypeVersion", target = "event.eventTypeVersion")
    @Mapping(source = "event.eventContent", target = "event.eventContent", qualifiedByName = "stringToJsonNode")
    @Mapping(source = "subscription.uuid", target = "subscription.uuid")
    @Mapping(source = "subscription.name", target = "subscription.name")
    @Mapping(source = "subscription.displayName", target = "subscription.displayName")
    @Mapping(source = "subscription.observerBaseUrl", target = "subscription.observerBaseUrl")
    @Mapping(source = "subscription.observerApiVersion", target = "subscription.observerApiVersion")
    NotificationDispatchRes toRes(Notification notification);
    
    @Named("stringToJsonNode")
    default JsonNode stringToJsonNode(String jsonString) {
        if (jsonString == null || jsonString.isEmpty()) {
            return null;
        }
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readTree(jsonString);
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse JSON string to JsonNode", e);
        }
    }
}
