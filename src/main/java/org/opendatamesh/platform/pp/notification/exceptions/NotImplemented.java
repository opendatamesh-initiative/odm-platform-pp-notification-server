package org.opendatamesh.platform.pp.notification.exceptions;

import org.springframework.http.HttpStatus;

public class NotImplemented extends NotificationApiException {
    public NotImplemented(String message) {
        super(message);
    }

    public NotImplemented(String message, Throwable cause) {
        super(message, cause);
    }

    @Override
    public HttpStatus getStatus() {
        return HttpStatus.NOT_IMPLEMENTED;
    }
}
