package org.opendatamesh.platform.pp.notification.subscription.services.usecases.observerregister;

import org.opendatamesh.platform.pp.notification.exceptions.BadRequestException;
import org.opendatamesh.platform.pp.notification.subscription.entities.Subscription;
import org.opendatamesh.platform.pp.notification.utils.usecases.TransactionalOutboundPort;
import org.opendatamesh.platform.pp.notification.utils.usecases.UseCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.util.Optional;

/**
 * Use case for managing observer registration to the notification service.
 * <p>
 * This use case handles observer registration and optionally handles
 * the observer base URL change.
 * </p>
 */
class ObserverRegister implements UseCase {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private static final String USE_CASE_PREFIX = "[ObserverRegister]";

    private final ObserverRegisterCommand command;
    private final ObserverRegisterPresenter presenter;

    private final ObserverRegisterPersistenceOutboundPort persistencePort;
    private final TransactionalOutboundPort transactionalOutboundPort;

    ObserverRegister(ObserverRegisterCommand command, ObserverRegisterPresenter presenter, ObserverRegisterPersistenceOutboundPort persistencePort, TransactionalOutboundPort transactionalOutboundPort) {
        this.command = command;
        this.presenter = presenter;
        this.persistencePort = persistencePort;
        this.transactionalOutboundPort = transactionalOutboundPort;
    }

    @Override
    public void execute() {
        validateCommand();
        transactionalOutboundPort.doInTransaction(() -> {
            Optional<Subscription> existingSubscription = persistencePort.findSubscriptionByName(command.observerName());
            if (existingSubscription.isPresent()) {
                updateExistingSubscription(existingSubscription.get());
                return;
            }
            createNewSubscription();
        });
    }

    private void createNewSubscription() {
        Subscription subscriptionToCreate = buildNewSubscriptionFromCommand();
        Subscription subscription = persistencePort.create(subscriptionToCreate);
        logger.info("{} Observer '{}' registered on '{}'", USE_CASE_PREFIX, subscription.getDisplayName(), subscription.getObserverServerBaseUrl());
        presenter.presentSubscription(subscription);
    }

    private Subscription buildNewSubscriptionFromCommand() {
        Subscription subscription = new Subscription();
        subscription.setName(command.observerName());
        if (StringUtils.hasText(command.observerDisplayName())) {
            subscription.setDisplayName(command.observerDisplayName());
        } else {
            subscription.setDisplayName(command.observerName());
        }
        subscription.setObserverServerBaseUrl(command.observerBaseUrl());
        return subscription;
    }

    private void updateExistingSubscription(Subscription subscription) {
        logger.warn("{} Observer '{}' already registered, changing base url from '{}' to '{}'", USE_CASE_PREFIX, subscription.getDisplayName(), subscription.getObserverServerBaseUrl(), command.observerBaseUrl());
        subscription.setObserverServerBaseUrl(command.observerBaseUrl());
        if (StringUtils.hasText(command.observerDisplayName())) {
            subscription.setDisplayName(command.observerDisplayName());
        }
        persistencePort.save(subscription);
        presenter.presentSubscription(subscription);
    }

    private void validateCommand() {
        if (command == null) {
            throw new BadRequestException("Command cannot be null");
        }
        if (!StringUtils.hasText(command.observerName())) {
            throw new BadRequestException("Observer name cannot be null or empty");
        }
        if (!StringUtils.hasText(command.observerBaseUrl())) {
            throw new BadRequestException("Observer base url cannot be null or empty");
        }

    }
}
