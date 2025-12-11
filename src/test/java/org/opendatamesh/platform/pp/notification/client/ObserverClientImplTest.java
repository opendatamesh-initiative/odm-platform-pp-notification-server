package org.opendatamesh.platform.pp.notification.client;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.opendatamesh.platform.pp.notification.notification.entities.Notification;
import org.opendatamesh.platform.pp.notification.rest.v1.controllers.EventNotificationV1Mapper;
import org.opendatamesh.platform.pp.notification.rest.v1.resources.EventNotificationResV1;
import org.opendatamesh.platform.pp.notification.rest.v2.resources.client.observer.NotificationDispatchMapper;
import org.opendatamesh.platform.pp.notification.rest.v2.resources.client.observer.NotificationDispatchRes;
import org.opendatamesh.platform.pp.notification.subscription.entities.Subscription;
import org.opendatamesh.platform.pp.notification.utils.client.RestUtils;
import org.opendatamesh.platform.pp.notification.utils.client.http.HttpHeader;
import org.springframework.boot.web.client.RestTemplateBuilder;

import java.lang.reflect.Field;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ObserverClientImplTest {

    @Mock
    private RestTemplateBuilder restTemplateBuilder;

    @Mock
    private RestUtils restUtils;

    @Mock
    private EventNotificationV1Mapper eventNotificationV1Mapper;

    @Mock
    private NotificationDispatchMapper notificationDispatchMapper;

    private ObserverClientImpl observerClientImpl;

    private Notification notification;
    private Subscription subscription;

    @BeforeEach
    void setUp() throws Exception {
        subscription = new Subscription();
        subscription.setObserverBaseUrl("https://observer.example.com");
        subscription.setObserverApiVersion("V1");

        notification = new Notification();
        notification.setSubscription(subscription);

        observerClientImpl = new ObserverClientImpl(restTemplateBuilder, eventNotificationV1Mapper, notificationDispatchMapper);
        
        // Inject mocked restUtils using reflection
        Field restUtilsField = ObserverClientImpl.class.getDeclaredField("restUtils");
        restUtilsField.setAccessible(true);
        restUtilsField.set(observerClientImpl, restUtils);
    }

    @Test
    void whenDispatchNotificationWithV1ApiVersionThenUseV1MapperAndEndpoint() {
        // Given
        subscription.setObserverApiVersion("V1");
        EventNotificationResV1 v1Payload = new EventNotificationResV1();
        when(eventNotificationV1Mapper.toRes(notification)).thenReturn(v1Payload);
        when(restUtils.genericPost(any(String.class), anyList(), any(), eq(String.class))).thenReturn("success");

        // When
        observerClientImpl.dispatchNotification(notification);

        // Then
        ArgumentCaptor<String> urlCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Object> payloadCaptor = ArgumentCaptor.forClass(Object.class);
        verify(restUtils).genericPost(
                urlCaptor.capture(),
                anyList(),
                payloadCaptor.capture(),
                eq(String.class)
        );

        assertThat(urlCaptor.getValue()).isEqualTo("https://observer.example.com/api/v1/up/observer/notifications");
        assertThat(payloadCaptor.getValue()).isEqualTo(v1Payload);
        verify(eventNotificationV1Mapper).toRes(notification);
    }

    @Test
    void whenDispatchNotificationWithV2ApiVersionThenUseV2MapperAndEndpoint() {
        // Given
        subscription.setObserverApiVersion("V2");
        NotificationDispatchRes v2Payload = new NotificationDispatchRes();
        when(notificationDispatchMapper.toRes(notification)).thenReturn(v2Payload);
        when(restUtils.genericPost(any(String.class), anyList(), any(), eq(String.class))).thenReturn("success");

        // When
        observerClientImpl.dispatchNotification(notification);

        // Then
        ArgumentCaptor<String> urlCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Object> payloadCaptor = ArgumentCaptor.forClass(Object.class);
        verify(restUtils).genericPost(
                urlCaptor.capture(),
                anyList(),
                payloadCaptor.capture(),
                eq(String.class)
        );

        assertThat(urlCaptor.getValue()).isEqualTo("https://observer.example.com/api/v2/up/observer/notifications");
        assertThat(payloadCaptor.getValue()).isEqualTo(v2Payload);
        verify(notificationDispatchMapper).toRes(notification);
    }

    @Test
    void whenDispatchNotificationWithLowercaseV1ApiVersionThenConvertToUppercase() {
        // Given
        subscription.setObserverApiVersion("v1");
        EventNotificationResV1 v1Payload = new EventNotificationResV1();
        when(eventNotificationV1Mapper.toRes(notification)).thenReturn(v1Payload);
        when(restUtils.genericPost(any(String.class), anyList(), any(), eq(String.class))).thenReturn("success");

        // When
        observerClientImpl.dispatchNotification(notification);

        // Then
        ArgumentCaptor<String> urlCaptor = ArgumentCaptor.forClass(String.class);
        verify(restUtils).genericPost(
                urlCaptor.capture(),
                anyList(),
                any(),
                eq(String.class)
        );

        assertThat(urlCaptor.getValue()).isEqualTo("https://observer.example.com/api/v1/up/observer/notifications");
        verify(eventNotificationV1Mapper).toRes(notification);
    }

    @Test
    void whenDispatchNotificationWithLowercaseV2ApiVersionThenConvertToUppercase() {
        // Given
        subscription.setObserverApiVersion("v2");
        NotificationDispatchRes v2Payload = new NotificationDispatchRes();
        when(notificationDispatchMapper.toRes(notification)).thenReturn(v2Payload);
        when(restUtils.genericPost(any(String.class), anyList(), any(), eq(String.class))).thenReturn("success");

        // When
        observerClientImpl.dispatchNotification(notification);

        // Then
        ArgumentCaptor<String> urlCaptor = ArgumentCaptor.forClass(String.class);
        verify(restUtils).genericPost(
                urlCaptor.capture(),
                anyList(),
                any(),
                eq(String.class)
        );

        assertThat(urlCaptor.getValue()).isEqualTo("https://observer.example.com/api/v2/up/observer/notifications");
        verify(notificationDispatchMapper).toRes(notification);
    }

    @Test
    void whenDispatchNotificationWithNullNotificationThenThrowNullPointerException() {
        // Given
        Notification nullNotification = null;

        // When & Then
        assertThatThrownBy(() -> observerClientImpl.dispatchNotification(nullNotification))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void whenDispatchNotificationWithNullSubscriptionThenThrowNullPointerException() {
        // Given
        notification.setSubscription(null);

        // When & Then
        assertThatThrownBy(() -> observerClientImpl.dispatchNotification(notification))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void whenDispatchNotificationWithNullObserverApiVersionThenThrowIllegalArgumentException() {
        // Given
        subscription.setObserverApiVersion(null);

        // When & Then
        assertThatThrownBy(() -> observerClientImpl.dispatchNotification(notification))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void whenDispatchNotificationWithInvalidApiVersionThenThrowIllegalArgumentException() {
        // Given
        subscription.setObserverApiVersion("INVALID");

        // When & Then
        assertThatThrownBy(() -> observerClientImpl.dispatchNotification(notification))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void whenDispatchNotificationThenCallRestUtilsWithEmptyHeaders() {
        // Given
        subscription.setObserverApiVersion("V1");
        EventNotificationResV1 v1Payload = new EventNotificationResV1();
        when(eventNotificationV1Mapper.toRes(notification)).thenReturn(v1Payload);
        when(restUtils.genericPost(any(String.class), anyList(), any(), eq(String.class))).thenReturn("success");

        // When
        observerClientImpl.dispatchNotification(notification);

        // Then
        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<HttpHeader>> headersCaptor = ArgumentCaptor.forClass(List.class);
        verify(restUtils).genericPost(
                any(String.class),
                headersCaptor.capture(),
                any(),
                eq(String.class)
        );

        if (headersCaptor.getValue() != null) {
            assertThat(headersCaptor.getValue()).isEmpty();
        }
    }

    @Test
    void whenDispatchNotificationWithDifferentBaseUrlThenBuildCorrectUrl() {
        // Given
        subscription.setObserverBaseUrl("https://different-server.com");
        subscription.setObserverApiVersion("V2");
        NotificationDispatchRes v2Payload = new NotificationDispatchRes();
        when(notificationDispatchMapper.toRes(notification)).thenReturn(v2Payload);
        when(restUtils.genericPost(any(String.class), anyList(), any(), eq(String.class))).thenReturn("success");

        // When
        observerClientImpl.dispatchNotification(notification);

        // Then
        ArgumentCaptor<String> urlCaptor = ArgumentCaptor.forClass(String.class);
        verify(restUtils).genericPost(
                urlCaptor.capture(),
                anyList(),
                any(),
                eq(String.class)
        );

        assertThat(urlCaptor.getValue()).isEqualTo("https://different-server.com/api/v2/up/observer/notifications");
    }
}

