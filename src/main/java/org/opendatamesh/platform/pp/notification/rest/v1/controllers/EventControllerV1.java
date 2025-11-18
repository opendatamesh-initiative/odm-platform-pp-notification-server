package org.opendatamesh.platform.pp.notification.rest.v1.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.opendatamesh.platform.pp.notification.event.services.core.EventService;
import org.opendatamesh.platform.pp.notification.rest.v1.resources.EventResV1;
import org.opendatamesh.platform.pp.notification.rest.v1.resources.EventV1SearchOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(
        value = "/api/v1/pp/notification/events"
)
@Validated
@Tag(name = "Events", description = "Endpoints for managing events")
public class EventControllerV1 {

    @Autowired
    private EventService eventService;

    @Autowired
    private EventNotificationV1Mapper eventNotificationV1Mapper;

    // ===============================================================================
    // GET /events/{eventId}
    // ===============================================================================

    @Operation(
            summary = "Get event by ID",
            description = "Retrieves a specific event by its ID"
    )
    @ResponseStatus(HttpStatus.OK)
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Event found",
                    content = @Content(schema = @Schema(implementation = EventResV1.class))
            ),
            @ApiResponse(responseCode = "404", description = "Event not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/{eventId}")
    public EventResV1 readOneEventEndpoint(
            @Parameter(description = "ID of the desired Event", required = true)
            @PathVariable(value = "eventId") Long eventId
    ) {
        // Get event entity (not resource) to use with the mapper
        var event = eventService.findOne(eventId);
        return eventNotificationV1Mapper.toEventResV1(event);
    }


    // ===============================================================================
    // GET /events
    // ===============================================================================

    @Operation(
            summary = "Search events",
            description = "Retrieves a paginated list of events based on search criteria"
    )
    @ResponseStatus(HttpStatus.OK)
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Events found",
                    content = @Content(schema = @Schema(implementation = Page.class))
            ),
            @ApiResponse(responseCode = "400", description = "Invalid search parameters"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping
    public Page<EventResV1> searchEventsEndpoint(
            @PageableDefault(size = 20, page = 0)
            Pageable pageable,
            EventV1SearchOptions searchOptions
    ) {
        // Convert v1 search options to v2 search options
        var eventSearchOptions = eventNotificationV1Mapper.toEventSearchOptions(searchOptions);
        
        // Get events as entities (not resources) to use with the mapper
        var eventsPage = eventService.findAllFiltered(pageable, eventSearchOptions);
        
        // Convert each event entity to v1 resource
        return eventsPage.map(eventNotificationV1Mapper::toEventResV1);
    }

}
