package org.opendatamesh.platform.pp.notification.notification.services.usecases.replay;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class NotificationReplayConfig {

    @Bean
    public NotificationReplayer notificationReplayer(
            NotificationReplayPersistenceOutboundPort persistencePort,
            NotificationReplayDeliveryOutboundPort deliveryPort
    ) {
        return new NotificationReplayerImpl(persistencePort, deliveryPort);
    }
}



