package org.opendatamesh.platform.pp.notification.rest.v1.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.opendatamesh.platform.pp.notification.notification.services.core.NotificationService;
import org.opendatamesh.platform.pp.notification.rest.v1.resources.EventNotificationResV1;
import org.opendatamesh.platform.pp.notification.rest.v1.resources.EventNotificationV1SearchOptions;
import org.opendatamesh.platform.pp.notification.rest.v2.resources.notification.NotificationRes;
import org.opendatamesh.platform.pp.notification.rest.v2.resources.notification.NotificationStatusRes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping(value = "/api/v1/pp/notification/notifications")
@Validated
@Tag(name = "Notifications", description = "Endpoints for managing notifications")
public class EventNotificationControllerV1 {

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private EventNotificationV1Mapper eventNotificationV1Mapper;

    // ===============================================================================
    // PUT /notifications/{notificationId}
    // ===============================================================================

    @Operation(
            summary = "Update notification",
            description = "Updates an existing notification by its ID"
    )
    @ResponseStatus(HttpStatus.OK)
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Notification updated successfully",
                    content = @Content(schema = @Schema(implementation = EventNotificationResV1.class))
            ),
            @ApiResponse(responseCode = "404", description = "Notification not found"),
            @ApiResponse(responseCode = "400", description = "Invalid request parameters"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PutMapping("/{id}")
    public EventNotificationResV1 updateEventNotificationEndpoint(
            @Parameter(description = "ID of the Notification to update", required = true)
            @PathVariable(value = "id") Long id,
            @Parameter(description = "Updated notification details", required = true)
            @RequestBody EventNotificationResV1 notification
    ) {
        // Get existing notification
        NotificationRes existingNotification = notificationService.findOneResource(id);
        
        // Update only the fields that can be updated (status and errorMessage/processingOutput)
        if (notification.getStatus() != null) {
            NotificationStatusRes statusRes = eventNotificationV1Mapper.mapToNotificationStatusRes(notification.getStatus());
            existingNotification.setStatus(statusRes);
        }
        if (notification.getProcessingOutput() != null) {
            existingNotification.setErrorMessage(notification.getProcessingOutput());
        }
        
        // Update the notification
        existingNotification.setSequenceId(id);
        notificationService.overwriteResource(id, existingNotification);
        
        // Convert back to v1 resource
        return eventNotificationV1Mapper.toRes(notificationService.findOne(id));
    }


    // ===============================================================================
    // GET /notifications/{notificationId}
    // ===============================================================================

    @Operation(
            summary = "Get notification by ID",
            description = "Retrieves a specific notification by its ID"
    )
    @ResponseStatus(HttpStatus.OK)
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Notification found",
                    content = @Content(schema = @Schema(implementation = EventNotificationResV1.class))
            ),
            @ApiResponse(responseCode = "404", description = "Notification not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/{notificationId}")
    public EventNotificationResV1 readOneEventNotificationEndpoint(
            @Parameter(description = "ID of the desired Notification", required = true)
            @PathVariable(value = "notificationId") Long notificationId
    ) {
        // Get notification entity (not resource) to use with the mapper
        var notification = notificationService.findOne(notificationId);
        return eventNotificationV1Mapper.toRes(notification);
    }


    // ===============================================================================
    // GET /notifications
    // ===============================================================================

    @Operation(
            summary = "Search notifications",
            description = "Retrieves a paginated list of notifications based on search criteria"
    )
    @ResponseStatus(HttpStatus.OK)
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Notifications found",
                    content = @Content(schema = @Schema(implementation = Page.class))
            ),
            @ApiResponse(responseCode = "400", description = "Invalid search parameters"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping
    public Page<EventNotificationResV1> searchEventNotificationsEndpoint(
            @PageableDefault(size = 20, page = 0)
            Pageable pageable,
            EventNotificationV1SearchOptions searchOptions
    ) {
        // Convert v1 search options to v2 search options
        var notificationSearchOptions = eventNotificationV1Mapper.toNotificationSearchOptions(searchOptions);
        
        // Get notifications as entities (not resources) to use with the mapper
        var notificationsPage = notificationService.findAllFiltered(pageable, notificationSearchOptions);
        
        // Convert each notification entity to v1 resource
        return notificationsPage.map(eventNotificationV1Mapper::toRes);
    }

}
