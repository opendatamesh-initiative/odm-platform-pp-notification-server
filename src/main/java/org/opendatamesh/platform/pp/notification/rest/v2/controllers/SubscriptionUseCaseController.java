package org.opendatamesh.platform.pp.notification.rest.v2.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.opendatamesh.platform.pp.notification.rest.v2.resources.subscription.usecases.subscribe.SubscribeCommandRes;
import org.opendatamesh.platform.pp.notification.rest.v2.resources.subscription.usecases.subscribe.SubscribeResponseRes;
import org.opendatamesh.platform.pp.notification.subscription.services.SubscriptionUtilsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/v2/pp/notification/subscriptions", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Subscriptions", description = "Endpoints for managing subscriptions")
public class SubscriptionUseCaseController {

    @Autowired
    private SubscriptionUtilsService utilsService;

    @Operation(summary = "Subscribes an observer", description = "Registers an observer and subscribes it to the specified event types. If the observer already exists, it will be updated. If event types are provided, the observer will be subscribed to those types and unsubscribed from any types not in the list.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Observer subscribed successfully",
                    content = @Content(schema = @Schema(implementation = SubscribeResponseRes.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request parameters"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/subscribe")
    @ResponseStatus(HttpStatus.CREATED)
    public SubscribeResponseRes subscribe(
            @Parameter(description = "Subscription command containing observer details (name, display name, base URL, API version) and list of event types to subscribe to", required = true)
            @RequestBody SubscribeCommandRes subscribeCommand
    ) {
        return utilsService.subscribe(subscribeCommand);
    }
}
