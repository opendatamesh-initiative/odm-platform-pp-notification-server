package org.opendatamesh.platform.pp.notification.rest.v2.resources.notification.usecases.replay;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "NotificationReplayCommandRes", description = "Command resource for replaying a failed notification. " +
        "Replays a notification that previously failed to deliver or process. The notification status will be reset " +
        "to PROCESSING and it will be dispatched again to the observer.")
public class NotificationReplayCommandRes {
    
    @Schema(
            description = "Unique identifier (sequence ID) of the notification to replay. " +
                    "The notification should have status FAILED_TO_DELIVER or FAILED_TO_PROCESS for optimal results, " +
                    "though other statuses are also accepted with a warning.",
            example = "101",
            required = true
    )
    private Long notificationSequenceId;

    public NotificationReplayCommandRes() {
    }

    public Long getNotificationSequenceId() {
        return notificationSequenceId;
    }

    public void setNotificationSequenceId(Long notificationSequenceId) {
        this.notificationSequenceId = notificationSequenceId;
    }
}
