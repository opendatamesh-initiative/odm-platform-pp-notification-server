package org.opendatamesh.platform.pp.notification.rest.v2.controllers;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.opendatamesh.platform.pp.notification.rest.v2.resources.subscription.SubscriptionRes;
import org.opendatamesh.platform.pp.notification.subscription.services.SubscriptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v2/pp/notification/subscriptions")
@Tag(name = "Subscriptions", description = "Subscription operations")
public class SubscriptionController {

    @Autowired
    private SubscriptionService subscriptionService;

    @Operation(summary = "Get all subscriptions", description = "Retrieve a paginated list of all subscriptions")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Subscriptions retrieved successfully",
                    content = @Content(schema = @Schema(implementation = Page.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping
    public Page<SubscriptionRes> searchSubscriptions(Pageable pageable) {
        return subscriptionService.findAllResources(pageable);
    }

    @Operation(summary = "Get subscription by ID", description = "Retrieve a specific subscription by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Subscription found",
                    content = @Content(schema = @Schema(implementation = SubscriptionRes.class))),
            @ApiResponse(responseCode = "404", description = "Subscription not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/{uuid}")
    public SubscriptionRes getSubscription(
            @Parameter(description = "Subscription ID") @PathVariable String uuid) {
        return subscriptionService.findOneResource(uuid);
    }

    @Operation(summary = "Create subscription", description = "Create a new subscription")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Subscription created successfully",
                    content = @Content(schema = @Schema(implementation = SubscriptionRes.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SubscriptionRes createSubscription(@RequestBody SubscriptionRes subscriptionRes) {
        return subscriptionService.createResource(subscriptionRes);
    }

    @Operation(summary = "Update subscription", description = "Update an existing subscription")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Subscription updated successfully",
                    content = @Content(schema = @Schema(implementation = SubscriptionRes.class))),
            @ApiResponse(responseCode = "404", description = "Subscription not found"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PutMapping("/{uuid}")
    public SubscriptionRes updateSubscription(
            @Parameter(description = "Subscription ID") @PathVariable String uuid,
            @RequestBody SubscriptionRes subscriptionRes) {
        return subscriptionService.overwriteResource(uuid, subscriptionRes);
    }

    @Operation(summary = "Delete subscription", description = "Delete an subscription by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Subscription deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Subscription not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @DeleteMapping("/{uuid}")
    public void deleteSubscription(
            @Parameter(description = "Subscription ID") @PathVariable String uuid) {
        subscriptionService.delete(uuid);
    }


}
