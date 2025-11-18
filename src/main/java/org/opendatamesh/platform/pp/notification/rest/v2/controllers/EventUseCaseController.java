package org.opendatamesh.platform.pp.notification.rest.v2.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.opendatamesh.platform.pp.notification.event.services.EventUtilsService;
import org.opendatamesh.platform.pp.notification.rest.v2.resources.event.usecases.emit.EventEmitCommandRes;
import org.opendatamesh.platform.pp.notification.rest.v2.resources.event.usecases.emit.EventEmitResponseRes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/v2/pp/notification/events", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Events", description = "Endpoints for managing events")
public class EventUseCaseController {

    @Autowired
    private EventUtilsService utilsService;

    @Operation(summary = "Emit an event", description = "Emits a new event to the notification system. The event will be stored and notifications will be sent to all subscribed observers that are interested in the event type.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Event emitted successfully",
                    content = @Content(schema = @Schema(implementation = EventEmitResponseRes.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request parameters"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/emit")
    @ResponseStatus(HttpStatus.CREATED)
    public EventEmitResponseRes emit(
            @Parameter(description = "Event emit command containing event details (resource type, resource identifier, event type, event type version, and event content)", required = true)
            @RequestBody EventEmitCommandRes emitCommand
    ) {
        return utilsService.emit(emitCommand);
    }
}
