package org.opendatamesh.platform.pp.notification.rest.v2.controllers;


import io.swagger.v3.oas.annotations.tags.Tag;
import org.opendatamesh.platform.pp.notification.notification.services.DispatchService;
import org.opendatamesh.platform.pp.notification.rest.v2.resources.event.EventRes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v2/pp/notification/dispatch")
@Tag(name = "Events", description = "Event management operations")
public class DispatchController {

    @Autowired
    private DispatchService dispatchService;

    @PostMapping("/dispatch")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void notifyEvent(@RequestBody EventRes eventResource) {
        dispatchService.notifyAll(eventResource);
    }
}
