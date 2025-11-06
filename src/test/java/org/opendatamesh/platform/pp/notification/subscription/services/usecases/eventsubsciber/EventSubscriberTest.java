package org.opendatamesh.platform.pp.notification.subscription.services.usecases.eventsubsciber;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.opendatamesh.platform.pp.notification.exceptions.BadRequestException;
import org.opendatamesh.platform.pp.notification.exceptions.NotFoundException;
import org.opendatamesh.platform.pp.notification.subscription.entities.Subscription;
import org.opendatamesh.platform.pp.notification.subscription.entities.SubscriptionEventType;
import org.opendatamesh.platform.pp.notification.utils.usecases.TransactionalOutboundPort;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventSubscriberTest {

    @Mock
    private EventSubscriberCommand command;

    @Mock
    private EventSubscriberPresenter presenter;

    @Mock
    private EventSubscriberPersistenceOutboundPort persistencePort;

    @Mock
    private TransactionalOutboundPort transactionalOutboundPort;

    @InjectMocks
    private EventSubscriber eventSubscriber;

    private Subscription subscription;
    private List<SubscriptionEventType> existingEventTypes;

    @BeforeEach
    void setUp() {
        subscription = new Subscription();
        subscription.setName("test-observer");
        subscription.setDisplayName("Test Observer");
        subscription.setObserverServerBaseUrl("https://observer.example.com");

        existingEventTypes = new ArrayList<>();
        subscription.setEventTypes(existingEventTypes);

        lenient().doAnswer(invocation -> {
            Runnable runnable = invocation.getArgument(0);
            runnable.run();
            return null;
        }).when(transactionalOutboundPort).doInTransaction(any(Runnable.class));
    }

    @Test
    void whenCommandIsNullThenThrowBadRequestException() {
        // Given
        EventSubscriber useCase = new EventSubscriber(null, presenter, persistencePort, transactionalOutboundPort);

        // When & Then
        assertThatThrownBy(useCase::execute)
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Command cannot be null");

        verifyNoInteractions(persistencePort);
        verifyNoInteractions(presenter);
    }

    @Test
    void whenObserverNameIsNullThenThrowBadRequestException() {
        // Given
        when(command.observerName()).thenReturn(null);

        // When & Then
        assertThatThrownBy(() -> eventSubscriber.execute())
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Observer name cannot be null or empty");

        verifyNoInteractions(persistencePort);
        verifyNoInteractions(presenter);
    }

    @Test
    void whenObserverNameIsEmptyThenThrowBadRequestException() {
        // Given
        when(command.observerName()).thenReturn("");

        // When & Then
        assertThatThrownBy(() -> eventSubscriber.execute())
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Observer name cannot be null or empty");

        verifyNoInteractions(persistencePort);
        verifyNoInteractions(presenter);
    }

    @Test
    void whenEventTypesIsNullThenThrowBadRequestException() {
        // Given
        when(command.observerName()).thenReturn("test-observer");
        when(command.eventTypes()).thenReturn(null);

        // When & Then
        assertThatThrownBy(() -> eventSubscriber.execute())
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Event types cannot be null or empty");

        verifyNoInteractions(persistencePort);
        verifyNoInteractions(presenter);
    }

    @Test
    void whenEventTypesIsEmptyThenThrowBadRequestException() {
        // Given
        when(command.observerName()).thenReturn("test-observer");
        when(command.eventTypes()).thenReturn(Set.of());

        // When & Then
        assertThatThrownBy(() -> eventSubscriber.execute())
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Event types cannot be null or empty");

        verifyNoInteractions(persistencePort);
        verifyNoInteractions(presenter);
    }

    @Test
    void whenObserverDoesNotExistThenThrowNotFoundException() {
        // Given
        when(command.observerName()).thenReturn("non-existent-observer");
        when(command.eventTypes()).thenReturn(Set.of("EVENT_TYPE_1"));
        when(persistencePort.findSubscriptionByName("non-existent-observer")).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> eventSubscriber.execute())
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Observer non-existent-observer does not exist.");

        verify(persistencePort).findSubscriptionByName("non-existent-observer");
        verify(persistencePort, never()).save(any(Subscription.class));
        verifyNoInteractions(presenter);
    }

    @Test
    void whenSubscribeToNewEventTypesThenAddEventTypesToSubscription() {
        // Given
        when(command.observerName()).thenReturn("test-observer");
        when(command.eventTypes()).thenReturn(Set.of("EVENT_TYPE_1", "EVENT_TYPE_2"));
        when(persistencePort.findSubscriptionByName("test-observer")).thenReturn(Optional.of(subscription));

        ArgumentCaptor<Subscription> subscriptionCaptor = ArgumentCaptor.forClass(Subscription.class);

        // When
        eventSubscriber.execute();

        // Then
        verify(persistencePort).findSubscriptionByName("test-observer");
        verify(persistencePort).save(subscriptionCaptor.capture());
        verify(presenter).presentUpdatedSubscription(subscriptionCaptor.capture());

        Subscription savedSubscription = subscriptionCaptor.getAllValues().get(0);
        assertThat(savedSubscription.getEventTypes()).hasSize(2);
        assertThat(savedSubscription.getEventTypes().stream()
                .map(SubscriptionEventType::getEventType)
                .toList())
                .containsExactlyInAnyOrder("EVENT_TYPE_1", "EVENT_TYPE_2");
    }

    @Test
    void whenSubscribeToExistingEventTypesThenDoNotAddDuplicates() {
        // Given
        existingEventTypes.add(new SubscriptionEventType("EVENT_TYPE_1"));
        subscription.setEventTypes(existingEventTypes);

        when(command.observerName()).thenReturn("test-observer");
        when(command.eventTypes()).thenReturn(Set.of("EVENT_TYPE_1", "EVENT_TYPE_2"));
        when(persistencePort.findSubscriptionByName("test-observer")).thenReturn(Optional.of(subscription));

        ArgumentCaptor<Subscription> subscriptionCaptor = ArgumentCaptor.forClass(Subscription.class);

        // When
        eventSubscriber.execute();

        // Then
        verify(persistencePort).findSubscriptionByName("test-observer");
        verify(persistencePort).save(subscriptionCaptor.capture());
        verify(presenter).presentUpdatedSubscription(subscriptionCaptor.capture());

        Subscription savedSubscription = subscriptionCaptor.getAllValues().get(0);
        assertThat(savedSubscription.getEventTypes()).hasSize(2);
        assertThat(savedSubscription.getEventTypes().stream()
                .map(SubscriptionEventType::getEventType)
                .toList())
                .containsExactlyInAnyOrder("EVENT_TYPE_1", "EVENT_TYPE_2");
    }

    @Test
    void whenSubscribeToMultipleNewEventTypesThenAddAllEventTypes() {
        // Given
        when(command.observerName()).thenReturn("test-observer");
        when(command.eventTypes()).thenReturn(Set.of("EVENT_TYPE_1", "EVENT_TYPE_2", "EVENT_TYPE_3"));
        when(persistencePort.findSubscriptionByName("test-observer")).thenReturn(Optional.of(subscription));

        ArgumentCaptor<Subscription> subscriptionCaptor = ArgumentCaptor.forClass(Subscription.class);

        // When
        eventSubscriber.execute();

        // Then
        verify(persistencePort).findSubscriptionByName("test-observer");
        verify(persistencePort).save(subscriptionCaptor.capture());
        verify(presenter).presentUpdatedSubscription(subscriptionCaptor.capture());

        Subscription savedSubscription = subscriptionCaptor.getAllValues().get(0);
        assertThat(savedSubscription.getEventTypes()).hasSize(3);
        assertThat(savedSubscription.getEventTypes().stream()
                .map(SubscriptionEventType::getEventType)
                .toList())
                .containsExactlyInAnyOrder("EVENT_TYPE_1", "EVENT_TYPE_2", "EVENT_TYPE_3");
    }

    @Test
    void whenSubscriptionHasNoEventTypesThenAddNewEventTypes() {
        // Given
        subscription.setEventTypes(new ArrayList<>());

        when(command.observerName()).thenReturn("test-observer");
        when(command.eventTypes()).thenReturn(Set.of("EVENT_TYPE_1"));
        when(persistencePort.findSubscriptionByName("test-observer")).thenReturn(Optional.of(subscription));

        ArgumentCaptor<Subscription> subscriptionCaptor = ArgumentCaptor.forClass(Subscription.class);

        // When
        eventSubscriber.execute();

        // Then
        verify(persistencePort).findSubscriptionByName("test-observer");
        verify(persistencePort).save(subscriptionCaptor.capture());
        verify(presenter).presentUpdatedSubscription(subscriptionCaptor.capture());

        Subscription savedSubscription = subscriptionCaptor.getAllValues().get(0);
        assertThat(savedSubscription.getEventTypes()).hasSize(1);
        assertThat(savedSubscription.getEventTypes().get(0).getEventType()).isEqualTo("EVENT_TYPE_1");
    }
}

