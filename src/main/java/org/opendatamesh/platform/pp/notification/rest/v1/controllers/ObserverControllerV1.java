package org.opendatamesh.platform.pp.notification.rest.v1.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.opendatamesh.platform.pp.notification.exceptions.NotFoundException;
import org.opendatamesh.platform.pp.notification.rest.v1.resources.ObserverResV1;
import org.opendatamesh.platform.pp.notification.rest.v1.resources.ObserverV1SearchOptions;
import org.opendatamesh.platform.pp.notification.rest.v2.resources.subscription.SubscriptionRes;
import org.opendatamesh.platform.pp.notification.rest.v2.resources.subscription.SubscriptionSearchOptions;
import org.opendatamesh.platform.pp.notification.rest.v2.resources.subscription.usecases.subscribe.SubscribeCommandRes;
import org.opendatamesh.platform.pp.notification.rest.v2.resources.subscription.usecases.subscribe.SubscribeResponseRes;
import org.opendatamesh.platform.pp.notification.subscription.services.SubscriptionUtilsService;
import org.opendatamesh.platform.pp.notification.subscription.services.core.SubscriptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping(
        value = "/api/v1/pp/notification/observers"
)
@Validated
@Tag(name = "Observers", description = "Endpoints for managing observers")
public class ObserverControllerV1 {

    @Autowired
    private SubscriptionService subscriptionService;

    @Autowired
    private SubscriptionUtilsService subscriptionUtilsService;

    @Autowired
    private ObserverV1Mapper observerV1Mapper;

    /**
     * Converts UUID string to Long ID using most significant bits.
     * This provides a deterministic mapping from UUID to Long.
     */
    private Long uuidToLongId(String uuid) {
        UUID uuidObj = UUID.fromString(uuid);
        return uuidObj.getMostSignificantBits();
    }

    /**
     * Finds UUID by Long ID by searching subscriptions.
     * Since we can't reverse the Long to UUID deterministically,
     * we search all subscriptions and match by most significant bits.
     */
    private String longIdToUuid(Long id) {
        // Search all subscriptions and find the one whose UUID's most significant bits match
        Page<SubscriptionRes> allSubscriptions = subscriptionService.findAllResourcesFiltered(
            org.springframework.data.domain.PageRequest.of(0, Integer.MAX_VALUE),
            new SubscriptionSearchOptions()
        );
        
        return allSubscriptions.getContent().stream()
            .filter(sub -> uuidToLongId(sub.getUuid()).equals(id))
            .map(SubscriptionRes::getUuid)
            .findFirst()
            .orElse(null);
    }

    // ===============================================================================
    // POST /observers
    // ===============================================================================

    @Operation(
            summary = "Create a new observer",
            description = "Creates a new observer in the notification system"
    )
    @ResponseStatus(HttpStatus.CREATED)
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Observer created successfully",
                    content = @Content(schema = @Schema(implementation = ObserverResV1.class))
            ),
            @ApiResponse(responseCode = "400", description = "Invalid request parameters"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping
    public ObserverResV1 addObserverEndpoint(
            @Parameter(description = "Observer details", required = true)
            @RequestBody ObserverResV1 observer
    ) {
        // Convert ObserverResV1 to SubscriptionRes
        SubscribeCommandRes subscribeCommand = observerV1Mapper.toSubscribeCommandRes(observer);
        
        // Create subscription using subscription service
        SubscribeResponseRes createdSubscription = subscriptionUtilsService.subscribe(subscribeCommand);
        
        // Convert UUID to Long id using deterministic mapping
        Long id = uuidToLongId(createdSubscription.getSubscription().getUuid());
        
        // Convert back to ObserverResV1 with Long id
        return observerV1Mapper.toObserverResV1(createdSubscription, id);
    }


    // ===============================================================================
    // PUT /observer/{id}
    // ===============================================================================

    @Operation(
            summary = "Update observer",
            description = "Updates an existing observer by its ID"
    )
    @ResponseStatus(HttpStatus.OK)
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Observer updated successfully",
                    content = @Content(schema = @Schema(implementation = ObserverResV1.class))
            ),
            @ApiResponse(responseCode = "404", description = "Observer not found"),
            @ApiResponse(responseCode = "400", description = "Invalid request parameters"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PutMapping("/{id}")
    public ObserverResV1 updateObserverEndpoint(
            @Parameter(description = "ID of the Observer to update", required = true)
            @PathVariable(value = "id") Long passedId,
            @Parameter(description = "Updated observer details", required = true)
            @RequestBody ObserverResV1 observer
    ) {
        getObserverEndpoint(passedId);
       // Convert ObserverResV1 to SubscriptionRes
       SubscribeCommandRes subscribeCommand = observerV1Mapper.toSubscribeCommandRes(observer);
        
       // Create subscription using subscription service
       SubscribeResponseRes createdSubscription = subscriptionUtilsService.subscribe(subscribeCommand);
       
       // Convert UUID to Long id using deterministic mapping
       Long id = uuidToLongId(createdSubscription.getSubscription().getUuid());
       
       // Convert back to ObserverResV1 with Long id
       return observerV1Mapper.toObserverResV1(createdSubscription, id);
    }


    // ===============================================================================
    // GET /observers
    // ===============================================================================

    @Operation(
            summary = "Search observers",
            description = "Retrieves a paginated list of observers based on search criteria"
    )
    @ResponseStatus(HttpStatus.OK)
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Observers found",
                    content = @Content(schema = @Schema(implementation = Page.class))
            ),
            @ApiResponse(responseCode = "400", description = "Invalid search parameters"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping
    public Page<ObserverResV1> getObserversEndpoint(
            @PageableDefault(size = 20, page = 0)
            Pageable pageable,
            ObserverV1SearchOptions searchOptions
    ) {
        // Convert ObserverV1SearchOptions to SubscriptionSearchOptions
        SubscriptionSearchOptions subscriptionSearchOptions = new SubscriptionSearchOptions();
        // Note: ObserverV1SearchOptions is currently empty, so no mapping needed
        
        // Get all subscriptions using subscription service
        Page<SubscriptionRes> subscriptionsPage = subscriptionService.findAllResourcesFiltered(pageable, subscriptionSearchOptions);
        
        // Convert each SubscriptionRes to ObserverResV1 with Long id
        return subscriptionsPage.map(subscriptionRes -> {
            // Convert UUID to Long id using deterministic mapping
            Long id = uuidToLongId(subscriptionRes.getUuid());
            return observerV1Mapper.toObserverResV1(subscriptionRes, id);
        });
    }


    // ===============================================================================
    // GET /observers/{id}
    // ===============================================================================

    @Operation(
            summary = "Get observer by ID",
            description = "Retrieves a specific observer by its ID"
    )
    @ResponseStatus(HttpStatus.OK)
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Observer found",
                    content = @Content(schema = @Schema(implementation = ObserverResV1.class))
            ),
            @ApiResponse(responseCode = "404", description = "Observer not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/{id}")
    public ObserverResV1 getObserverEndpoint(
            @Parameter(description = "ID of the desired Observer", required = true)
            @PathVariable(value = "id") Long id
    ) {
        // Get UUID from Long id using deterministic reverse lookup
        String uuid = longIdToUuid(id);
        if (uuid == null) {
            throw new NotFoundException("Observer with id " + id + " not found");
        }
        
        // Get subscription using subscription service
        SubscriptionRes subscriptionRes = subscriptionService.findOneResource(uuid);
        
        // Convert to ObserverResV1 with Long id
        return observerV1Mapper.toObserverResV1(subscriptionRes, id);
    }


    // ===============================================================================
    // DELETE /observers/{id}
    // ===============================================================================

    @Operation(
            summary = "Delete observer",
            description = "Deletes an observer by its ID"
    )
    @ResponseStatus(HttpStatus.OK)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Observer deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Observer not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @DeleteMapping(value = "/{id}")
    public void removeObserverEndpoint(
            @Parameter(description = "ID of the Observer to delete", required = true)
            @PathVariable(value = "id") Long id
    ) {
        // Get UUID from Long id using deterministic reverse lookup
        String uuid = longIdToUuid(id);
        if (uuid == null) {
            throw new NotFoundException("Observer with id " + id + " not found");
        }
        
        // Delete subscription using subscription service
        subscriptionService.delete(uuid);
    }

}
