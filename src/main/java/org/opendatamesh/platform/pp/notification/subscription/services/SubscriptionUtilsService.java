package org.opendatamesh.platform.pp.notification.subscription.services;

import org.opendatamesh.platform.pp.notification.rest.v2.resources.subscription.usecases.subscribe.SubscribeCommandRes;
import org.opendatamesh.platform.pp.notification.rest.v2.resources.subscription.usecases.subscribe.SubscribeMapper;
import org.opendatamesh.platform.pp.notification.rest.v2.resources.subscription.usecases.subscribe.SubscribeResponseRes;
import org.opendatamesh.platform.pp.notification.subscription.entities.Subscription;
import org.opendatamesh.platform.pp.notification.subscription.entities.SubscriptionEventType;
import org.opendatamesh.platform.pp.notification.subscription.services.usecases.eventsubscriber.EventSubscriberCommand;
import org.opendatamesh.platform.pp.notification.subscription.services.usecases.eventsubscriber.EventSubscriberFactory;
import org.opendatamesh.platform.pp.notification.subscription.services.usecases.eventunsubscriber.EventUnSubscriberCommand;
import org.opendatamesh.platform.pp.notification.subscription.services.usecases.eventunsubscriber.EventUnSubscriberFactory;
import org.opendatamesh.platform.pp.notification.subscription.services.usecases.observerregister.ObserverRegisterCommand;
import org.opendatamesh.platform.pp.notification.subscription.services.usecases.observerregister.ObserverRegisterFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Service
public class SubscriptionUtilsService {

    @Autowired
    private EventSubscriberFactory eventSubscriberFactory;
    @Autowired
    private EventUnSubscriberFactory eventUnSubscriberFactory;
    @Autowired
    private ObserverRegisterFactory observerRegisterFactory;
    @Autowired
    private SubscribeMapper mapper;

    public SubscribeResponseRes subscribe(SubscribeCommandRes subscribeCommand) {
        AtomicReference<Subscription> subscription = new AtomicReference<>();
        handleRegistration(subscribeCommand, subscription);

        if (subscribeCommand.getEventTypes() == null) {
            return mapper.toRes(subscription.get());
        }

        unsubscribeFromEventTypesNotInCommand(subscribeCommand, subscription);
        subscribeToEventTypes(subscribeCommand, subscription);

        return mapper.toRes(subscription.get());
    }

    private void subscribeToEventTypes(SubscribeCommandRes subscribeCommand, AtomicReference<Subscription> subscription) {
        if (!subscribeCommand.getEventTypes().isEmpty()) {
            eventSubscriberFactory.buildEventSubscriber(
                    new EventSubscriberCommand(subscribeCommand.getName(), Set.copyOf(subscribeCommand.getEventTypes())),
                    subscription::set
            ).execute();
        }
    }

    private void unsubscribeFromEventTypesNotInCommand(SubscribeCommandRes subscribeCommand, AtomicReference<Subscription> subscription) {
        Set<String> eventTypesToUnsubscribe = subscription.get().getEventTypes().stream()
                .map(SubscriptionEventType::getEventType)
                .filter(eventType -> !subscribeCommand.getEventTypes().contains(eventType))
                .collect(Collectors.toSet());
        
        if (!eventTypesToUnsubscribe.isEmpty()) {
            eventUnSubscriberFactory.buildEventUnSubscriber(
                    new EventUnSubscriberCommand(
                            subscribeCommand.getName(),
                            eventTypesToUnsubscribe
                    ),
                    subscription::set
            ).execute();
        }
    }

    private void handleRegistration(SubscribeCommandRes subscribeCommand, AtomicReference<Subscription> subscription) {
        ObserverRegisterCommand registerCommand = new ObserverRegisterCommand(
                subscribeCommand.getName(),
                subscribeCommand.getDisplayName(),
                subscribeCommand.getObserverBaseUrl(),
                subscribeCommand.getObserverApiVersion()
        );
        observerRegisterFactory.buildObserverRegister(registerCommand, subscription::set)
                .execute();
    }
}
