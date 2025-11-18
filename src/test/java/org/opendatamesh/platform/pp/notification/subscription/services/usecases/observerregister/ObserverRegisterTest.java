package org.opendatamesh.platform.pp.notification.subscription.services.usecases.observerregister;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.opendatamesh.platform.pp.notification.exceptions.BadRequestException;
import org.opendatamesh.platform.pp.notification.subscription.entities.Subscription;
import org.opendatamesh.platform.pp.notification.utils.usecases.TransactionalOutboundPort;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ObserverRegisterTest {

    @Mock
    private ObserverRegisterCommand command;

    @Mock
    private ObserverRegisterPresenter presenter;

    @Mock
    private ObserverRegisterPersistenceOutboundPort persistencePort;

    @Mock
    private TransactionalOutboundPort transactionalOutboundPort;

    @InjectMocks
    private ObserverRegister observerRegister;

    private Subscription existingSubscription;

    @BeforeEach
    void setUp() {
        existingSubscription = new Subscription();
        existingSubscription.setName("test-observer");
        existingSubscription.setDisplayName("Test Observer");
        existingSubscription.setObserverServerBaseUrl("https://old-url.example.com");

        lenient().doAnswer(invocation -> {
            Runnable runnable = invocation.getArgument(0);
            runnable.run();
            return null;
        }).when(transactionalOutboundPort).doInTransaction(any(Runnable.class));
    }

    @Test
    void whenCommandIsNullThenThrowBadRequestException() {
        // Given
        ObserverRegister useCase = new ObserverRegister(null, presenter, persistencePort, transactionalOutboundPort);

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
        assertThatThrownBy(() -> observerRegister.execute())
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
        assertThatThrownBy(() -> observerRegister.execute())
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Observer name cannot be null or empty");

        verifyNoInteractions(persistencePort);
        verifyNoInteractions(presenter);
    }

    @Test
    void whenObserverBaseUrlIsNullThenThrowBadRequestException() {
        // Given
        when(command.observerName()).thenReturn("test-observer");
        when(command.observerBaseUrl()).thenReturn(null);

        // When & Then
        assertThatThrownBy(() -> observerRegister.execute())
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Observer base url cannot be null or empty");

        verifyNoInteractions(persistencePort);
        verifyNoInteractions(presenter);
    }

    @Test
    void whenObserverBaseUrlIsEmptyThenThrowBadRequestException() {
        // Given
        when(command.observerName()).thenReturn("test-observer");
        when(command.observerBaseUrl()).thenReturn("");

        // When & Then
        assertThatThrownBy(() -> observerRegister.execute())
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Observer base url cannot be null or empty");

        verifyNoInteractions(persistencePort);
        verifyNoInteractions(presenter);
    }

    @Test
    void whenObserverDoesNotExistThenCreateNewSubscription() {
        // Given
        when(command.observerName()).thenReturn("new-observer");
        when(command.observerDisplayName()).thenReturn("New Observer");
        when(command.observerBaseUrl()).thenReturn("https://observer.example.com");
        when(persistencePort.findSubscriptionByName("new-observer")).thenReturn(Optional.empty());

        ArgumentCaptor<Subscription> subscriptionCaptor = ArgumentCaptor.forClass(Subscription.class);
        Subscription createdSubscription = new Subscription();
        createdSubscription.setName("new-observer");
        createdSubscription.setDisplayName("New Observer");
        createdSubscription.setObserverServerBaseUrl("https://observer.example.com");
        when(persistencePort.create(any(Subscription.class))).thenReturn(createdSubscription);

        // When
        observerRegister.execute();

        // Then
        verify(persistencePort).findSubscriptionByName("new-observer");
        verify(persistencePort).create(subscriptionCaptor.capture());
        verify(persistencePort, never()).save(any(Subscription.class));
        verify(presenter).presentSubscription(createdSubscription);

        Subscription capturedSubscription = subscriptionCaptor.getValue();
        assertThat(capturedSubscription.getName()).isEqualTo("new-observer");
        assertThat(capturedSubscription.getDisplayName()).isEqualTo("New Observer");
        assertThat(capturedSubscription.getObserverServerBaseUrl()).isEqualTo("https://observer.example.com");
    }

    @Test
    void whenObserverDoesNotExistAndDisplayNameIsNullThenUseObserverNameAsDisplayName() {
        // Given
        when(command.observerName()).thenReturn("new-observer");
        when(command.observerDisplayName()).thenReturn(null);
        when(command.observerBaseUrl()).thenReturn("https://observer.example.com");
        when(persistencePort.findSubscriptionByName("new-observer")).thenReturn(Optional.empty());

        ArgumentCaptor<Subscription> subscriptionCaptor = ArgumentCaptor.forClass(Subscription.class);
        Subscription createdSubscription = new Subscription();
        createdSubscription.setName("new-observer");
        createdSubscription.setDisplayName("new-observer");
        createdSubscription.setObserverServerBaseUrl("https://observer.example.com");
        when(persistencePort.create(any(Subscription.class))).thenReturn(createdSubscription);

        // When
        observerRegister.execute();

        // Then
        verify(persistencePort).findSubscriptionByName("new-observer");
        verify(persistencePort).create(subscriptionCaptor.capture());
        verify(presenter).presentSubscription(createdSubscription);

        Subscription capturedSubscription = subscriptionCaptor.getValue();
        assertThat(capturedSubscription.getName()).isEqualTo("new-observer");
        assertThat(capturedSubscription.getDisplayName()).isEqualTo("new-observer");
        assertThat(capturedSubscription.getObserverServerBaseUrl()).isEqualTo("https://observer.example.com");
    }

    @Test
    void whenObserverDoesNotExistAndDisplayNameIsEmptyThenUseObserverNameAsDisplayName() {
        // Given
        when(command.observerName()).thenReturn("new-observer");
        when(command.observerDisplayName()).thenReturn("");
        when(command.observerBaseUrl()).thenReturn("https://observer.example.com");
        when(persistencePort.findSubscriptionByName("new-observer")).thenReturn(Optional.empty());

        ArgumentCaptor<Subscription> subscriptionCaptor = ArgumentCaptor.forClass(Subscription.class);
        Subscription createdSubscription = new Subscription();
        createdSubscription.setName("new-observer");
        createdSubscription.setDisplayName("new-observer");
        createdSubscription.setObserverServerBaseUrl("https://observer.example.com");
        when(persistencePort.create(any(Subscription.class))).thenReturn(createdSubscription);

        // When
        observerRegister.execute();

        // Then
        verify(persistencePort).findSubscriptionByName("new-observer");
        verify(persistencePort).create(subscriptionCaptor.capture());
        verify(presenter).presentSubscription(createdSubscription);

        Subscription capturedSubscription = subscriptionCaptor.getValue();
        assertThat(capturedSubscription.getName()).isEqualTo("new-observer");
        assertThat(capturedSubscription.getDisplayName()).isEqualTo("new-observer");
    }

    @Test
    void whenObserverExistsThenUpdateBaseUrl() {
        // Given
        when(command.observerName()).thenReturn("test-observer");
        when(command.observerBaseUrl()).thenReturn("https://new-url.example.com");
        when(persistencePort.findSubscriptionByName("test-observer")).thenReturn(Optional.of(existingSubscription));

        ArgumentCaptor<Subscription> subscriptionCaptor = ArgumentCaptor.forClass(Subscription.class);

        // When
        observerRegister.execute();

        // Then
        verify(persistencePort).findSubscriptionByName("test-observer");
        verify(persistencePort, never()).create(any(Subscription.class));
        verify(persistencePort).save(subscriptionCaptor.capture());
        verify(presenter).presentSubscription(subscriptionCaptor.capture());

        Subscription savedSubscription = subscriptionCaptor.getAllValues().get(0);
        assertThat(savedSubscription.getObserverServerBaseUrl()).isEqualTo("https://new-url.example.com");
        assertThat(savedSubscription.getName()).isEqualTo("test-observer");
    }

    @Test
    void whenObserverExistsAndDisplayNameProvidedThenUpdateDisplayName() {
        // Given
        when(command.observerName()).thenReturn("test-observer");
        when(command.observerDisplayName()).thenReturn("Updated Display Name");
        when(command.observerBaseUrl()).thenReturn("https://new-url.example.com");
        when(persistencePort.findSubscriptionByName("test-observer")).thenReturn(Optional.of(existingSubscription));

        ArgumentCaptor<Subscription> subscriptionCaptor = ArgumentCaptor.forClass(Subscription.class);

        // When
        observerRegister.execute();

        // Then
        verify(persistencePort).findSubscriptionByName("test-observer");
        verify(persistencePort).save(subscriptionCaptor.capture());
        verify(presenter).presentSubscription(subscriptionCaptor.capture());

        Subscription savedSubscription = subscriptionCaptor.getAllValues().get(0);
        assertThat(savedSubscription.getObserverServerBaseUrl()).isEqualTo("https://new-url.example.com");
        assertThat(savedSubscription.getDisplayName()).isEqualTo("Updated Display Name");
    }

    @Test
    void whenObserverExistsAndDisplayNameIsNullThenKeepExistingDisplayName() {
        // Given
        when(command.observerName()).thenReturn("test-observer");
        when(command.observerDisplayName()).thenReturn(null);
        when(command.observerBaseUrl()).thenReturn("https://new-url.example.com");
        when(persistencePort.findSubscriptionByName("test-observer")).thenReturn(Optional.of(existingSubscription));

        ArgumentCaptor<Subscription> subscriptionCaptor = ArgumentCaptor.forClass(Subscription.class);

        // When
        observerRegister.execute();

        // Then
        verify(persistencePort).findSubscriptionByName("test-observer");
        verify(persistencePort).save(subscriptionCaptor.capture());
        verify(presenter).presentSubscription(subscriptionCaptor.capture());

        Subscription savedSubscription = subscriptionCaptor.getAllValues().get(0);
        assertThat(savedSubscription.getObserverServerBaseUrl()).isEqualTo("https://new-url.example.com");
        assertThat(savedSubscription.getDisplayName()).isEqualTo("Test Observer");
    }

    @Test
    void whenObserverExistsAndDisplayNameIsEmptyThenKeepExistingDisplayName() {
        // Given
        when(command.observerName()).thenReturn("test-observer");
        when(command.observerDisplayName()).thenReturn("");
        when(command.observerBaseUrl()).thenReturn("https://new-url.example.com");
        when(persistencePort.findSubscriptionByName("test-observer")).thenReturn(Optional.of(existingSubscription));

        ArgumentCaptor<Subscription> subscriptionCaptor = ArgumentCaptor.forClass(Subscription.class);

        // When
        observerRegister.execute();

        // Then
        verify(persistencePort).findSubscriptionByName("test-observer");
        verify(persistencePort).save(subscriptionCaptor.capture());
        verify(presenter).presentSubscription(subscriptionCaptor.capture());

        Subscription savedSubscription = subscriptionCaptor.getAllValues().get(0);
        assertThat(savedSubscription.getObserverServerBaseUrl()).isEqualTo("https://new-url.example.com");
        assertThat(savedSubscription.getDisplayName()).isEqualTo("Test Observer");
    }
}

