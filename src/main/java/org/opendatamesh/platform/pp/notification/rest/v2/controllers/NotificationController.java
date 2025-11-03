package org.opendatamesh.platform.pp.notification.rest.v2.controllers;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.opendatamesh.platform.pp.notification.notification.services.core.NotificationServiceImpl;
import org.opendatamesh.platform.pp.notification.rest.v2.resources.notification.NotificationRes;
import org.opendatamesh.platform.pp.notification.rest.v2.resources.notification.NotificationSearchOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v2/pp/notification/notifications")
@Tag(name = "Notifications", description = "Notifications management operations")
public class NotificationController {

    @Autowired
    private NotificationServiceImpl notificationService;

    @Operation(summary = "Get all notifications", description = "Retrieve a paginated list of all notifications, optionally filtered by subscription UUID, notification status, or event type")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Notifications retrieved successfully",
                    content = @Content(schema = @Schema(implementation = Page.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping
    public Page<NotificationRes> searchNotifications(
            @Parameter(description = "Search filters") NotificationSearchOptions filters,
            Pageable pageable) {
        if (filters != null && (filters.getSubscriptionUuid() != null || filters.getNotificationStatus() != null || filters.getEventType() != null)) {
            return notificationService.findAllResourcesFiltered(pageable, filters);
        }
        return notificationService.findAllResources(pageable);
    }

    @Operation(summary = "Get notification by ID", description = "Retrieve a specific notification by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Notification found",
                    content = @Content(schema = @Schema(implementation = NotificationRes.class))),
            @ApiResponse(responseCode = "404", description = "Notification not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/{id}")
    public NotificationRes getNotification(
            @Parameter(description = "Notification ID") @PathVariable Long id) {
        return notificationService.findOneResource(id);
    }

    @Operation(summary = "Create notification", description = "Create a new notification")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Notification created successfully",
                    content = @Content(schema = @Schema(implementation = NotificationRes.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping
    public NotificationRes createNotification(@RequestBody NotificationRes notificationRes) {
        return notificationService.createResource(notificationRes);
    }

    @Operation(summary = "Update notification", description = "Update an existing notification")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Notification updated successfully",
                    content = @Content(schema = @Schema(implementation = NotificationRes.class))),
            @ApiResponse(responseCode = "404", description = "Notification not found"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PutMapping("/{id}")
    public NotificationRes updateNotification(
            @Parameter(description = "Notification ID") @PathVariable Long id,
            @RequestBody NotificationRes notificationRes) {
        return notificationService.overwriteResource(id, notificationRes);
    }

    @Operation(summary = "Delete notification", description = "Delete an notification by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Notification deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Notification not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @DeleteMapping("/{id}")
    public void deleteNotification(
            @Parameter(description = "Notification ID") @PathVariable Long id) {
        notificationService.delete(id);
    }


}
