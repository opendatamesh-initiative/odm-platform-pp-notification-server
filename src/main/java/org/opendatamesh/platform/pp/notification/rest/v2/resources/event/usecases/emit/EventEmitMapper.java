package org.opendatamesh.platform.pp.notification.rest.v2.resources.event.usecases.emit;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.opendatamesh.platform.pp.notification.event.entities.Event;

@Mapper(componentModel = "spring")
public interface EventEmitMapper {

    @Mapping(source = "event.resourceType", target = "resourceType")
    @Mapping(source = "event.resourceIdentifier", target = "resourceIdentifier")
    @Mapping(source = "event.type", target = "type")
    @Mapping(source = "event.eventTypeVersion", target = "eventTypeVersion")
    @Mapping(source = "event.eventContent", target = "eventContent", qualifiedByName = "jsonNodeToString")
    @Mapping(target = "sequenceId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Event toEntity(EventEmitCommandRes commandRes);

    @Mapping(source = "sequenceId", target = "event.sequenceId")
    @Mapping(source = "resourceType", target = "event.resourceType")
    @Mapping(source = "resourceIdentifier", target = "event.resourceIdentifier")
    @Mapping(source = "type", target = "event.type")
    @Mapping(source = "eventTypeVersion", target = "event.eventTypeVersion")
    @Mapping(source = "eventContent", target = "event.eventContent", qualifiedByName = "stringToJsonNode")
    @Mapping(source = "createdAt", target = "event.createdAt")
    @Mapping(source = "updatedAt", target = "event.updatedAt")
    EventEmitResponseRes toRes(Event entity);

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

