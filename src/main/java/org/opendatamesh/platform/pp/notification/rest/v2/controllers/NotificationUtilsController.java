package org.opendatamesh.platform.pp.notification.rest.v2.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.opendatamesh.platform.pp.notification.notification.services.NotificationUtilsService;
import org.opendatamesh.platform.pp.notification.rest.v2.resources.notification.NotificationRes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v2/pp/notification/notifications")
@Tag(name = "Notification Utils", description = "Notification utility operations")
public class NotificationUtilsController {

    @Autowired
    private NotificationUtilsService notificationUtilsService;

    @Operation(summary = "Replay notification", description = "Replay a specific notification to its original subscriber (non-broadcast)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "202", description = "Notification replay initiated successfully",
                    content = @Content(schema = @Schema(implementation = NotificationRes.class))),
            @ApiResponse(responseCode = "404", description = "Notification not found"),
            @ApiResponse(responseCode = "400", description = "Invalid notification or missing subscription/event"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/{notificationId}/replay")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public NotificationRes replayNotification(
            @Parameter(description = "Notification ID to replay") @PathVariable Long notificationId) {
        return notificationUtilsService.replay(notificationId);
    }
}
