package org.opendatamesh.platform.pp.notification.event.services.usecases.dispatch;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataEventDispatcherConfig {

    @Bean
    public DataEventDispatcher dataEventDispatcher(
            DataEventDispatcherPersistenceOutboundPort persistencePort,
            DataEventDispatcherNotificationOutboundPort notificationPort
    ) {
        return new DataEventDispatcherImpl(persistencePort, notificationPort);
    }
}


