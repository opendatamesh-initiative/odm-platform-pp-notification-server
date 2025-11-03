package org.opendatamesh.platform.pp.notification.subscription.services.usecases.create;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SubscriptionCreatorConfig {

    @Bean
    public SubscriptionCreator subscriptionCreator(SubscriptionCreatorPersistenceOutboundPort persistencePort) {
        return new SubscriptionCreatorImpl(persistencePort);
    }
}


