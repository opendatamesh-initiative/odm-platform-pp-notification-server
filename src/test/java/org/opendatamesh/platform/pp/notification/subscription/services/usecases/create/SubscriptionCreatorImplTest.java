package org.opendatamesh.platform.pp.notification.subscription.services.usecases.create;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SubscriptionCreatorImplTest {

    @Test
    void createsSubscriptionAndReturnsUuid() {
        SubscriptionCreatorPersistenceOutboundPort port = s -> {
            s.setUuid("11111111-1111-1111-1111-111111111111");
            return s;
        };

        SubscriptionCreatorImpl creator = new SubscriptionCreatorImpl(port);

        StringBuilder result = new StringBuilder();
        creator.execute(new SubscriptionCreateCommand(
                "sub-name",
                "Sub Display",
                "https://observer.example.com",
                List.of("DATA_PRODUCT_CREATED")
        ), new SubscriptionCreatorPresenter() {
            @Override
            public void success(String subscriptionUuid) {
                result.append("OK");
            }

            @Override
            public void failure(Throwable error) {
                result.append("KO");
            }
        });

        assertEquals("OK", result.toString());
    }
}


