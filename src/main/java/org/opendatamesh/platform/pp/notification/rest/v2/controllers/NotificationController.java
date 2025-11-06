package org.opendatamesh.platform.pp.notification.rest.v2.controllers;

import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.opendatamesh.platform.pp.notification.notification.services.core.NotificationService;
import org.opendatamesh.platform.pp.notification.rest.v2.resources.notification.NotificationRes;
import org.opendatamesh.platform.pp.notification.rest.v2.resources.notification.NotificationSearchOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/v2/pp/notification/notifications", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Notifications", description = "Endpoints for managing notifications")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @Operation(summary = "Create a new notification", description = "Creates a new notification in the notification system")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Notification created successfully",
                    content = @Content(schema = @Schema(implementation = NotificationRes.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request parameters"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Hidden
    public NotificationRes createNotification(
            @Parameter(description = "Notification details", required = true)
            @RequestBody NotificationRes notification
    ) {
        return notificationService.createResource(notification);
    }

    @Operation(summary = "Get notification by ID", description = "Retrieves a specific notification by its sequence ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Notification found",
                    content = @Content(schema = @Schema(implementation = NotificationRes.class))),
            @ApiResponse(responseCode = "404", description = "Notification not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/{sequenceId}")
    @ResponseStatus(HttpStatus.OK)
    public NotificationRes getNotification(
            @Parameter(description = "Notification sequence ID", required = true)
            @PathVariable("sequenceId") Long sequenceId
    ) {
        return notificationService.findOneResource(sequenceId);
    }

    @Operation(summary = "Search notifications", description = "Retrieves a paginated list of notifications based on search criteria. " +
            "The results can be sorted by any of the following properties: sequenceId, status, createdAt, updatedAt. " +
            "Sort direction can be specified using 'asc' or 'desc' (e.g., 'sort=createdAt,desc').")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Notifications found",
                    content = @Content(schema = @Schema(implementation = Page.class))),
            @ApiResponse(responseCode = "400", description = "Invalid search parameters or invalid sort property"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Page<NotificationRes> searchNotifications(
            @Parameter(description = "Search options for filtering notifications")
            NotificationSearchOptions searchOptions,
            @Parameter(description = "Pagination and sorting parameters. Default sort is by createdAt in descending order. " +
                    "Valid sort properties are: sequenceId, status, createdAt, updatedAt")
            @PageableDefault(page = 0, size = 20, sort = "createdAt", direction = Sort.Direction.DESC)
            Pageable pageable
    ) {
        return notificationService.findAllResourcesFiltered(pageable, searchOptions);
    }

    @Operation(summary = "Update notification", description = "Updates an existing notification by its sequence ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Notification updated successfully",
                    content = @Content(schema = @Schema(implementation = NotificationRes.class))),
            @ApiResponse(responseCode = "404", description = "Notification not found"),
            @ApiResponse(responseCode = "400", description = "Invalid request parameters"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PutMapping("/{sequenceId}")
    @ResponseStatus(HttpStatus.OK)
    @Hidden
    public NotificationRes updateNotification(
            @Parameter(description = "Notification sequence ID", required = true)
            @PathVariable("sequenceId") Long sequenceId,
            @Parameter(description = "Updated notification details", required = true)
            @RequestBody NotificationRes notification
    ) {
        notification.setSequenceId(sequenceId);
        return notificationService.overwriteResource(sequenceId, notification);
    }

    @Operation(summary = "Delete notification", description = "Deletes a notification by its sequence ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Notification deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Notification not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @DeleteMapping("/{sequenceId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Hidden
    public void deleteNotification(
            @Parameter(description = "Notification sequence ID", required = true)
            @PathVariable("sequenceId") Long sequenceId
    ) {
        notificationService.delete(sequenceId);
    }
}

