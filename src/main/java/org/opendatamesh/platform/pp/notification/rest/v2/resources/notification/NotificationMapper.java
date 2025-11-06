package org.opendatamesh.platform.pp.notification.rest.v2.resources.notification;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.opendatamesh.platform.pp.notification.notification.entities.Notification;

@Mapper(componentModel = "spring")
public interface NotificationMapper {

    @Mapping(source = "event.eventContent", target = "event.eventContent", qualifiedByName = "stringToJsonNode")
    NotificationRes toRes(Notification notification);

    @Mapping(source = "event.eventContent", target = "event.eventContent", qualifiedByName = "jsonNodeToString")
    Notification toEntity(NotificationRes notificationRes);

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

    @Named("jsonNodeToString")
    default String jsonNodeToString(JsonNode jsonNode) {
        if (jsonNode == null) {
            return null;
        }
        return jsonNode.toString();
    }
}
