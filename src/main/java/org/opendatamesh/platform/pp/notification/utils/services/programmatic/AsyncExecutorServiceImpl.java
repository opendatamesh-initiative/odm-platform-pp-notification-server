package org.opendatamesh.platform.pp.notification.utils.services.programmatic;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class AsyncExecutorServiceImpl implements AsyncExecutorService {

    @Override
    @Async
    public void execute(Runnable runnable) {
        runnable.run();
    }
}
