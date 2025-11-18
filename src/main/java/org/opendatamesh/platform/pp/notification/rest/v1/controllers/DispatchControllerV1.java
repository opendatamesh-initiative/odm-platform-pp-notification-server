package org.opendatamesh.platform.pp.notification.rest.v1.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.opendatamesh.platform.pp.notification.event.services.EventUtilsService;
import org.opendatamesh.platform.pp.notification.rest.v1.resources.EventResV1;
import org.opendatamesh.platform.pp.notification.rest.v2.resources.event.usecases.emit.EventEmitCommandRes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/v1/pp/notification/dispatch")
@Validated
@Tag(name = "Dispatch", description = "Endpoints for dispatching events to observers")
public class DispatchControllerV1 {

    @Autowired
    private EventUtilsService eventUtilsService;

    @Autowired
    private DispatchV1Mapper dispatchV1Mapper;

    // ===============================================================================
    // POST /dispatch
    // ===============================================================================

    @Operation(
            summary = "Dispatch an event",
            description = "Dispatches an event to all registered observers"
    )
    @ResponseStatus(HttpStatus.OK)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Event dispatched successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request parameters"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping
    public void notifyEventEndpoint(
            @Parameter(description = "Event details", required = true)
            @RequestBody EventResV1 event
    ) {
        // Convert EventResV1 to EventEmitCommandRes
        EventEmitCommandRes emitCommand = dispatchV1Mapper.toEventEmitCommandRes(event);
        
        // Emit the event using EventUtilsService (this will create notifications for subscribed observers)
        eventUtilsService.emit(emitCommand);
    }

}
