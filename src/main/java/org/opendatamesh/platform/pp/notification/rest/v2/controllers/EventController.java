package org.opendatamesh.platform.pp.notification.rest.v2.controllers;

import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.opendatamesh.platform.pp.notification.event.services.core.EventService;
import org.opendatamesh.platform.pp.notification.rest.v2.resources.event.EventRes;
import org.opendatamesh.platform.pp.notification.rest.v2.resources.event.EventSearchOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/v2/pp/notification/events", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Events", description = "Endpoints for managing events")
public class EventController {

    @Autowired
    private EventService eventService;

    @Operation(summary = "Create a new event", description = "Creates a new event in the notification system")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Event created successfully",
                    content = @Content(schema = @Schema(implementation = EventRes.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request parameters"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Hidden
    public EventRes createEvent(
            @Parameter(description = "Event details", required = true)
            @RequestBody EventRes event
    ) {
        return eventService.createResource(event);
    }

    @Operation(summary = "Get event by ID", description = "Retrieves a specific event by its sequence ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Event found",
                    content = @Content(schema = @Schema(implementation = EventRes.class))),
            @ApiResponse(responseCode = "404", description = "Event not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/{sequenceId}")
    @ResponseStatus(HttpStatus.OK)
    public EventRes getEvent(
            @Parameter(description = "Event sequence ID", required = true)
            @PathVariable("sequenceId") Long sequenceId
    ) {
        return eventService.findOneResource(sequenceId);
    }

    @Operation(summary = "Search events", description = "Retrieves a paginated list of events based on search criteria. " +
            "The results can be sorted by any of the following properties: sequenceId, resourceType, resourceIdentifier, " +
            "type, eventTypeVersion, createdAt, updatedAt. Sort direction can be specified using 'asc' or 'desc' (e.g., 'sort=createdAt,desc').")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Events found",
                    content = @Content(schema = @Schema(implementation = Page.class))),
            @ApiResponse(responseCode = "400", description = "Invalid search parameters or invalid sort property"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Page<EventRes> searchEvents(
            @Parameter(description = "Search options for filtering events")
            EventSearchOptions searchOptions,
            @Parameter(description = "Pagination and sorting parameters. Default sort is by createdAt in descending order. " +
                    "Valid sort properties are: sequenceId, resourceType, resourceIdentifier, type, eventTypeVersion, createdAt, updatedAt")
            @PageableDefault(page = 0, size = 20, sort = "createdAt", direction = Sort.Direction.DESC)
            Pageable pageable
    ) {
        return eventService.findAllResourcesFiltered(pageable, searchOptions);
    }

    @Operation(summary = "Update event", description = "Updates an existing event by its sequence ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Event updated successfully",
                    content = @Content(schema = @Schema(implementation = EventRes.class))),
            @ApiResponse(responseCode = "404", description = "Event not found"),
            @ApiResponse(responseCode = "400", description = "Invalid request parameters"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PutMapping("/{sequenceId}")
    @ResponseStatus(HttpStatus.OK)
    @Hidden
    public EventRes updateEvent(
            @Parameter(description = "Event sequence ID", required = true)
            @PathVariable("sequenceId") Long sequenceId,
            @Parameter(description = "Updated event details", required = true)
            @RequestBody EventRes event
    ) {
        event.setSequenceId(sequenceId);
        return eventService.overwriteResource(sequenceId, event);
    }

    @Operation(summary = "Delete event", description = "Deletes an event by its sequence ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Event deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Event not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @DeleteMapping("/{sequenceId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Hidden
    public void deleteEvent(
            @Parameter(description = "Event sequence ID", required = true)
            @PathVariable("sequenceId") Long sequenceId
    ) {
        eventService.delete(sequenceId);
    }
}

