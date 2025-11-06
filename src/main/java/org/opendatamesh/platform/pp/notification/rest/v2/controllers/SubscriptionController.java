package org.opendatamesh.platform.pp.notification.rest.v2.controllers;

import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.opendatamesh.platform.pp.notification.rest.v2.resources.subscription.SubscriptionRes;
import org.opendatamesh.platform.pp.notification.rest.v2.resources.subscription.SubscriptionSearchOptions;
import org.opendatamesh.platform.pp.notification.subscription.services.core.SubscriptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/v2/pp/notification/subscriptions", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Subscriptions", description = "Endpoints for managing subscriptions")
public class SubscriptionController {

    @Autowired
    private SubscriptionService subscriptionService;

    @Operation(summary = "Get subscription by UUID", description = "Retrieves a specific subscription by its UUID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Subscription found",
                    content = @Content(schema = @Schema(implementation = SubscriptionRes.class))),
            @ApiResponse(responseCode = "404", description = "Subscription not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/{uuid}")
    @ResponseStatus(HttpStatus.OK)
    public SubscriptionRes getSubscription(
            @Parameter(description = "Subscription UUID", required = true)
            @PathVariable("uuid") String uuid
    ) {
        return subscriptionService.findOneResource(uuid);
    }

    @Operation(summary = "Search subscriptions", description = "Retrieves a paginated list of subscriptions based on search criteria. " +
            "The results can be sorted by any of the following properties: uuid, observerName, displayName, createdAt, updatedAt. " +
            "Sort direction can be specified using 'asc' or 'desc' (e.g., 'sort=observerName,desc').")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Subscriptions found",
                    content = @Content(schema = @Schema(implementation = Page.class))),
            @ApiResponse(responseCode = "400", description = "Invalid search parameters or invalid sort property"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Page<SubscriptionRes> searchSubscriptions(
            @Parameter(description = "Search options for filtering subscriptions")
            SubscriptionSearchOptions searchOptions,
            @Parameter(description = "Pagination and sorting parameters. Default sort is by createdAt in descending order. " +
                    "Valid sort properties are: uuid, observerName, displayName, createdAt, updatedAt")
            @PageableDefault(page = 0, size = 20, sort = "createdAt", direction = Sort.Direction.DESC)
            Pageable pageable
    ) {
        return subscriptionService.findAllResourcesFiltered(pageable, searchOptions);
    }

    @Operation(summary = "Delete subscription", description = "Deletes a subscription by its UUID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Subscription deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Subscription not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @DeleteMapping("/{uuid}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteSubscription(
            @Parameter(description = "Subscription UUID", required = true)
            @PathVariable("uuid") String uuid
    ) {
        subscriptionService.delete(uuid);
    }

    @Operation(summary = "Create a new subscription", description = "Creates a new subscription in the notification system")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Subscription created successfully",
                    content = @Content(schema = @Schema(implementation = SubscriptionRes.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request parameters"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Hidden
    public SubscriptionRes createSubscription(
            @Parameter(description = "Subscription details", required = true)
            @RequestBody SubscriptionRes subscription
    ) {
        return subscriptionService.createResource(subscription);
    }

    @Operation(summary = "Update subscription", description = "Updates an existing subscription by its UUID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Subscription updated successfully",
                    content = @Content(schema = @Schema(implementation = SubscriptionRes.class))),
            @ApiResponse(responseCode = "404", description = "Subscription not found"),
            @ApiResponse(responseCode = "400", description = "Invalid request parameters"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PutMapping("/{uuid}")
    @ResponseStatus(HttpStatus.OK)
    @Hidden
    public SubscriptionRes updateSubscription(
            @Parameter(description = "Subscription UUID", required = true)
            @PathVariable("uuid") String uuid,
            @Parameter(description = "Updated subscription details", required = true)
            @RequestBody SubscriptionRes subscription
    ) {
        subscription.setUuid(uuid);
        return subscriptionService.overwriteResource(uuid, subscription);
    }
}

