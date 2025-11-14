package org.opendatamesh.platform.pp.notification.rest.v2.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.opendatamesh.platform.pp.notification.notification.services.NotificationUtilsService;
import org.opendatamesh.platform.pp.notification.rest.v2.resources.notification.usecases.replay.NotificationReplayCommandRes;
import org.opendatamesh.platform.pp.notification.rest.v2.resources.notification.usecases.replay.NotificationReplayResponseRes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/v2/pp/notification/notifications", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Notifications", description = "Endpoints for managing notifications")
public class NotificationUseCaseController {

    @Autowired
    private NotificationUtilsService notificationUtilsService;

    @Operation(
            summary = "Replay a notification",
            description = "Replays a notification by resetting its status to PROCESSING and dispatching it again to the observer. " +
                    "This operation is typically used for notifications that failed to deliver (FAILED_TO_DELIVER) or failed to process (FAILED_TO_PROCESS). " +
                    "Notifications with other statuses can also be replayed, but a warning will be logged. " +
                    "The notification's error message is cleared during replay and will be populated again only if the dispatch fails."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Notification replayed successfully. The notification status has been reset to PROCESSING and dispatched to the observer."),
            @ApiResponse(responseCode = "400", description = "Invalid request parameters (e.g., null notification sequence ID)"),
            @ApiResponse(responseCode = "404", description = "Notification not found with the provided sequence ID"),
            @ApiResponse(responseCode = "500", description = "Internal server error occurred during notification replay")
    })
    @PostMapping("/replay")
    @ResponseStatus(HttpStatus.OK)
    public NotificationReplayResponseRes replay(
            @Parameter(
                    description = "Notification replay command containing the notification sequence ID to replay. " +
                            "The notification should ideally have status FAILED_TO_DELIVER or FAILED_TO_PROCESS, " +
                            "though other statuses are also accepted with a warning.",
                    required = true
            )
            @RequestBody NotificationReplayCommandRes notificationReplayCommand
    ) {
        return notificationUtilsService.replay(notificationReplayCommand);
    }
}

