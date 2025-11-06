package org.opendatamesh.platform.pp.notification.event.services.usecases.emit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.opendatamesh.platform.pp.notification.client.ObserverClient;
import org.opendatamesh.platform.pp.notification.event.entities.Event;
import org.opendatamesh.platform.pp.notification.exceptions.BadRequestException;
import org.opendatamesh.platform.pp.notification.notification.entities.Notification;
import org.opendatamesh.platform.pp.notification.notification.entities.NotificationStatus;
import org.opendatamesh.platform.pp.notification.subscription.entities.Subscription;
import org.opendatamesh.platform.pp.notification.utils.services.programmatic.AsyncExecutorService;
import org.opendatamesh.platform.pp.notification.utils.services.programmatic.EntityInitAndDetachService;
import org.opendatamesh.platform.pp.notification.utils.usecases.TransactionalOutboundPort;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EventEmitterTest {

    @Mock
    private EventEmitterCommand command;

    @Mock
    private EventEmitterPresenter presenter;

    @Mock
    private TransactionalOutboundPort transactionalPort;

    @Mock
    private AsyncExecutorService asyncExecutor;

    @Mock
    private EntityInitAndDetachService entityDetacher;

    @Mock
    private EventEmitterPersistenceOutboundPort persistencePort;

    @Mock
    private EventEmitterSubscriptionOutboundPort subscriptionPort;

    @Mock
    private EventEmitterNotificationOutboundPort notificationPort;

    @Mock
    private ObserverClient observerClient;

    @InjectMocks
    private EventEmitter eventEmitter;

    private Event event;
    private Subscription subscription;

    @BeforeEach
    void setUp() {
        event = new Event();
        event.setType("DATA_PRODUCT_CREATED");
        event.setEventTypeVersion("1.0.0");
        event.setEventContent("{\"test\": \"content\"}");
        event.setResourceType("DATA_PRODUCT");
        event.setResourceIdentifier("test-id");

        subscription = new Subscription();
        subscription.setName("test-observer");
        subscription.setDisplayName("Test Observer");
        subscription.setObserverServerBaseUrl("https://observer.example.com");

        lenient().doAnswer(invocation -> {
            Runnable runnable = invocation.getArgument(0);
            runnable.run();
            return null;
        }).when(asyncExecutor).execute(any(Runnable.class));
    }

    private void mockTransactionalPortForEventCreation() {
        doAnswer(invocation -> {
            @SuppressWarnings("unchecked")
            Function<Void, Event> function = invocation.getArgument(0);
            return function.apply(null);
        }).when(transactionalPort).doInTransactionWithResults(any(Function.class), any());
    }

    private void mockTransactionalPortForRunnable() {
        doAnswer(invocation -> {
            Runnable runnable = invocation.getArgument(0);
            runnable.run();
            return null;
        }).when(transactionalPort).doInTransaction(any(Runnable.class));
    }

    @Test
    void whenCommandIsNullThenThrowBadRequestException() {
        // Given
        EventEmitter useCase = new EventEmitter(null, presenter, transactionalPort, asyncExecutor, entityDetacher, persistencePort, subscriptionPort, notificationPort, observerClient);

        // When & Then
        assertThatThrownBy(useCase::execute)
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Command cannot be null");

        verifyNoInteractions(persistencePort);
        verifyNoInteractions(presenter);
        verifyNoInteractions(asyncExecutor);
    }

    @Test
    void whenEventIsNullThenThrowBadRequestException() {
        // Given
        when(command.event()).thenReturn(null);

        // When & Then
        assertThatThrownBy(() -> eventEmitter.execute())
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Event cannot be null");

        verifyNoInteractions(persistencePort);
        verifyNoInteractions(presenter);
        verifyNoInteractions(asyncExecutor);
    }

    @Test
    void whenEventTypeIsNullThenThrowBadRequestException() {
        // Given
        event.setType(null);
        when(command.event()).thenReturn(event);

        // When & Then
        assertThatThrownBy(() -> eventEmitter.execute())
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Event type cannot be null");

        verifyNoInteractions(persistencePort);
        verifyNoInteractions(presenter);
        verifyNoInteractions(asyncExecutor);
    }

    @Test
    void whenEventTypeVersionIsNullThenThrowBadRequestException() {
        // Given
        event.setEventTypeVersion(null);
        when(command.event()).thenReturn(event);

        // When & Then
        assertThatThrownBy(() -> eventEmitter.execute())
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Event must have a version number.");

        verifyNoInteractions(persistencePort);
        verifyNoInteractions(presenter);
        verifyNoInteractions(asyncExecutor);
    }

    @Test
    void whenEventTypeVersionIsEmptyThenThrowBadRequestException() {
        // Given
        event.setEventTypeVersion("");
        when(command.event()).thenReturn(event);

        // When & Then
        assertThatThrownBy(() -> eventEmitter.execute())
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Event must have a version number.");

        verifyNoInteractions(persistencePort);
        verifyNoInteractions(presenter);
        verifyNoInteractions(asyncExecutor);
    }

    @Test
    void whenEventContentIsNullThenThrowBadRequestException() {
        // Given
        event.setEventContent(null);
        when(command.event()).thenReturn(event);

        // When & Then
        assertThatThrownBy(() -> eventEmitter.execute())
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Event content cannot be null or empty");

        verifyNoInteractions(persistencePort);
        verifyNoInteractions(presenter);
        verifyNoInteractions(asyncExecutor);
    }

    @Test
    void whenEventContentIsEmptyThenThrowBadRequestException() {
        // Given
        event.setEventContent("");
        when(command.event()).thenReturn(event);

        // When & Then
        assertThatThrownBy(() -> eventEmitter.execute())
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Event content cannot be null or empty");

        verifyNoInteractions(persistencePort);
        verifyNoInteractions(presenter);
        verifyNoInteractions(asyncExecutor);
    }

    @Test
    void whenEmitEventThenCreateEventAndProcessSubscriptions() {
        // Given
        when(command.event()).thenReturn(event);
        Event savedEvent = new Event();
        savedEvent.setSequenceId(1L);
        savedEvent.setType("DATA_PRODUCT_CREATED");
        savedEvent.setEventTypeVersion("1.0.0");
        savedEvent.setEventContent("{\"test\": \"content\"}");

        mockTransactionalPortForEventCreation();

        when(persistencePort.create(event)).thenReturn(savedEvent);
        when(subscriptionPort.findGenericSubscription()).thenReturn(new ArrayList<>());
        when(subscriptionPort.findSubscriptionByEventType("DATA_PRODUCT_CREATED")).thenReturn(new ArrayList<>());

        // When
        eventEmitter.execute();

        // Then
        ArgumentCaptor<Event> eventCaptor = ArgumentCaptor.forClass(Event.class);
        verify(persistencePort).create(eventCaptor.capture());
        verify(entityDetacher).initializeEntityAndDetach(savedEvent);
        verify(presenter).presentCreatedEvent(savedEvent);
        verify(subscriptionPort).findGenericSubscription();
        verify(subscriptionPort).findSubscriptionByEventType("DATA_PRODUCT_CREATED");
    }

    @Test
    void whenEmitEventWithGenericSubscriptionsThenCreateNotifications() {
        // Given
        when(command.event()).thenReturn(event);
        Event savedEvent = new Event();
        savedEvent.setSequenceId(1L);
        savedEvent.setType("DATA_PRODUCT_CREATED");

        List<Subscription> genericSubscriptions = new ArrayList<>();
        genericSubscriptions.add(subscription);

        mockTransactionalPortForEventCreation();

        when(persistencePort.create(event)).thenReturn(savedEvent);
        when(subscriptionPort.findGenericSubscription()).thenReturn(genericSubscriptions);
        when(subscriptionPort.findSubscriptionByEventType("DATA_PRODUCT_CREATED")).thenReturn(new ArrayList<>());

        Notification notification = new Notification();
        notification.setSequenceId(1L);
        notification.setEvent(savedEvent);
        notification.setSubscription(subscription);
        notification.setStatus(NotificationStatus.PROCESSING);

        when(notificationPort.create(any(Notification.class))).thenReturn(notification);

        // When
        eventEmitter.execute();

        // Then
        ArgumentCaptor<Notification> notificationCaptor = ArgumentCaptor.forClass(Notification.class);
        verify(notificationPort).create(notificationCaptor.capture());
        verify(observerClient).dispatchNotification(notificationCaptor.capture());
        verify(entityDetacher).initializeEntityAndDetach(notificationCaptor.capture());

        Notification capturedNotification = notificationCaptor.getAllValues().get(0);
        assertThat(capturedNotification.getEvent()).isEqualTo(savedEvent);
        assertThat(capturedNotification.getSubscription()).isEqualTo(subscription);
        assertThat(capturedNotification.getStatus()).isEqualTo(NotificationStatus.PROCESSING);
    }

    @Test
    void whenEmitEventWithSpecificSubscriptionsThenCreateNotifications() {
        // Given
        when(command.event()).thenReturn(event);
        Event savedEvent = new Event();
        savedEvent.setSequenceId(1L);
        savedEvent.setType("DATA_PRODUCT_CREATED");

        List<Subscription> specificSubscriptions = new ArrayList<>();
        specificSubscriptions.add(subscription);

        mockTransactionalPortForEventCreation();

        when(persistencePort.create(event)).thenReturn(savedEvent);
        when(subscriptionPort.findGenericSubscription()).thenReturn(new ArrayList<>());
        when(subscriptionPort.findSubscriptionByEventType("DATA_PRODUCT_CREATED")).thenReturn(specificSubscriptions);

        Notification notification = new Notification();
        notification.setSequenceId(1L);
        notification.setEvent(savedEvent);
        notification.setSubscription(subscription);
        notification.setStatus(NotificationStatus.PROCESSING);

        when(notificationPort.create(any(Notification.class))).thenReturn(notification);

        // When
        eventEmitter.execute();

        // Then
        ArgumentCaptor<Notification> notificationCaptor = ArgumentCaptor.forClass(Notification.class);
        verify(notificationPort).create(notificationCaptor.capture());
        verify(observerClient).dispatchNotification(notificationCaptor.capture());

        Notification capturedNotification = notificationCaptor.getAllValues().get(0);
        assertThat(capturedNotification.getEvent()).isEqualTo(savedEvent);
        assertThat(capturedNotification.getSubscription()).isEqualTo(subscription);
    }

    @Test
    void whenDispatchNotificationFailsThenUpdateNotificationStatusToFailed() {
        // Given
        when(command.event()).thenReturn(event);
        Event savedEvent = new Event();
        savedEvent.setSequenceId(1L);
        savedEvent.setType("DATA_PRODUCT_CREATED");

        List<Subscription> subscriptions = new ArrayList<>();
        subscriptions.add(subscription);

        mockTransactionalPortForEventCreation();
        mockTransactionalPortForRunnable();

        when(persistencePort.create(event)).thenReturn(savedEvent);
        when(subscriptionPort.findGenericSubscription()).thenReturn(subscriptions);
        when(subscriptionPort.findSubscriptionByEventType("DATA_PRODUCT_CREATED")).thenReturn(new ArrayList<>());

        Notification notification = new Notification();
        notification.setSequenceId(1L);
        notification.setEvent(savedEvent);
        notification.setSubscription(subscription);
        notification.setStatus(NotificationStatus.PROCESSING);

        when(notificationPort.create(any(Notification.class))).thenReturn(notification);
        when(notificationPort.findById(1L)).thenReturn(notification);
        doThrow(new RuntimeException("Dispatch failed")).when(observerClient).dispatchNotification(any(Notification.class));

        // When
        eventEmitter.execute();

        // Then
        ArgumentCaptor<Notification> notificationCaptor = ArgumentCaptor.forClass(Notification.class);
        verify(notificationPort).create(notificationCaptor.capture());
        verify(observerClient).dispatchNotification(notificationCaptor.capture());
        verify(notificationPort).findById(1L);
        verify(notificationPort).update(notificationCaptor.capture());

        Notification updatedNotification = notificationCaptor.getAllValues().get(2);
        assertThat(updatedNotification.getStatus()).isEqualTo(NotificationStatus.FAILED_TO_DELIVER);
        assertThat(updatedNotification.getErrorMessage()).isEqualTo("Dispatch failed");
    }

    @Test
    void whenDispatchNotificationFailsAndNotificationStatusIsNotPROCESSINGThenDoNotUpdate() {
        // Given
        when(command.event()).thenReturn(event);
        Event savedEvent = new Event();
        savedEvent.setSequenceId(1L);
        savedEvent.setType("DATA_PRODUCT_CREATED");

        List<Subscription> subscriptions = new ArrayList<>();
        subscriptions.add(subscription);

        mockTransactionalPortForEventCreation();
        mockTransactionalPortForRunnable();

        when(persistencePort.create(event)).thenReturn(savedEvent);
        when(subscriptionPort.findGenericSubscription()).thenReturn(subscriptions);
        when(subscriptionPort.findSubscriptionByEventType("DATA_PRODUCT_CREATED")).thenReturn(new ArrayList<>());

        Notification notification = new Notification();
        notification.setSequenceId(1L);
        notification.setEvent(savedEvent);
        notification.setSubscription(subscription);
        notification.setStatus(NotificationStatus.PROCESSING);

        Notification updatedNotification = new Notification();
        updatedNotification.setSequenceId(1L);
        updatedNotification.setStatus(NotificationStatus.PROCESSED);

        when(notificationPort.create(any(Notification.class))).thenReturn(notification);
        when(notificationPort.findById(1L)).thenReturn(updatedNotification);
        doThrow(new RuntimeException("Dispatch failed")).when(observerClient).dispatchNotification(any(Notification.class));

        // When
        eventEmitter.execute();

        // Then
        verify(notificationPort).findById(1L);
        verify(notificationPort, never()).update(any(Notification.class));
    }

    @Test
    void whenDispatchNotificationFailsAndNotificationIsNullThenDoNotUpdate() {
        // Given
        when(command.event()).thenReturn(event);
        Event savedEvent = new Event();
        savedEvent.setSequenceId(1L);
        savedEvent.setType("DATA_PRODUCT_CREATED");

        List<Subscription> subscriptions = new ArrayList<>();
        subscriptions.add(subscription);

        mockTransactionalPortForEventCreation();
        mockTransactionalPortForRunnable();

        when(persistencePort.create(event)).thenReturn(savedEvent);
        when(subscriptionPort.findGenericSubscription()).thenReturn(subscriptions);
        when(subscriptionPort.findSubscriptionByEventType("DATA_PRODUCT_CREATED")).thenReturn(new ArrayList<>());

        Notification notification = new Notification();
        notification.setSequenceId(1L);
        notification.setEvent(savedEvent);
        notification.setSubscription(subscription);
        notification.setStatus(NotificationStatus.PROCESSING);

        when(notificationPort.create(any(Notification.class))).thenReturn(notification);
        when(notificationPort.findById(1L)).thenReturn(null);
        doThrow(new RuntimeException("Dispatch failed")).when(observerClient).dispatchNotification(any(Notification.class));

        // When
        eventEmitter.execute();

        // Then
        verify(notificationPort).findById(1L);
        verify(notificationPort, never()).update(any(Notification.class));
    }

    @Test
    void whenEmitEventWithMultipleSubscriptionsThenCreateNotificationsForAll() {
        // Given
        when(command.event()).thenReturn(event);
        Event savedEvent = new Event();
        savedEvent.setSequenceId(1L);
        savedEvent.setType("DATA_PRODUCT_CREATED");

        Subscription subscription1 = new Subscription();
        subscription1.setName("observer-1");
        subscription1.setObserverServerBaseUrl("https://observer1.example.com");

        Subscription subscription2 = new Subscription();
        subscription2.setName("observer-2");
        subscription2.setObserverServerBaseUrl("https://observer2.example.com");

        List<Subscription> subscriptions = new ArrayList<>();
        subscriptions.add(subscription1);
        subscriptions.add(subscription2);

        mockTransactionalPortForEventCreation();

        when(persistencePort.create(event)).thenReturn(savedEvent);
        when(subscriptionPort.findGenericSubscription()).thenReturn(subscriptions);
        when(subscriptionPort.findSubscriptionByEventType("DATA_PRODUCT_CREATED")).thenReturn(new ArrayList<>());

        Notification notification1 = new Notification();
        notification1.setSequenceId(1L);
        notification1.setEvent(savedEvent);
        notification1.setSubscription(subscription1);
        notification1.setStatus(NotificationStatus.PROCESSING);

        Notification notification2 = new Notification();
        notification2.setSequenceId(2L);
        notification2.setEvent(savedEvent);
        notification2.setSubscription(subscription2);
        notification2.setStatus(NotificationStatus.PROCESSING);

        when(notificationPort.create(any(Notification.class)))
                .thenReturn(notification1)
                .thenReturn(notification2);

        // When
        eventEmitter.execute();

        // Then
        ArgumentCaptor<Notification> notificationCaptor = ArgumentCaptor.forClass(Notification.class);
        verify(notificationPort, org.mockito.Mockito.times(2)).create(notificationCaptor.capture());
        verify(observerClient, org.mockito.Mockito.times(2)).dispatchNotification(notificationCaptor.capture());

        List<Notification> capturedNotifications = notificationCaptor.getAllValues();
        assertThat(capturedNotifications).hasSize(4); // 2 creates + 2 dispatches
    }
}

