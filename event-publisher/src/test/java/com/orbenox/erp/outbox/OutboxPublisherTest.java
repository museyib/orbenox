package com.orbenox.erp.outbox;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.domain.Limit;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OutboxPublisherTest {

    @Mock
    private OutboxEventRepository outboxEventRepository;

    @Mock
    private RabbitTemplate rabbitTemplate;

    @InjectMocks
    private OutboxPublisher outboxPublisher;

    @Test
    void publishEvents_whenPendingEventsExist_shouldMarkEachAsPublished() {
        OutboxEvent firstEvent = eventWithId(1L);
        OutboxEvent secondEvent = eventWithId(2L);
        when(outboxEventRepository.findAllByStatusOrderByCreatedAt("PENDING", Limit.of(10)))
                .thenReturn(List.of(firstEvent, secondEvent));

        outboxPublisher.publishEvents();

        verify(outboxEventRepository).findAllByStatusOrderByCreatedAt("PENDING", Limit.of(10));
        verify(outboxEventRepository).updateStatus(1L, "PUBLISHED");
        verify(outboxEventRepository).updateStatus(2L, "PUBLISHED");
        verify(rabbitTemplate).convertAndSend("notification.queue", messageFor(firstEvent));
        verify(rabbitTemplate).convertAndSend("notification.queue", messageFor(secondEvent));
        verifyNoMoreInteractions(outboxEventRepository);
        verifyNoMoreInteractions(rabbitTemplate);
    }

    @Test
    void publishEvents_whenNoPendingEventsExist_shouldNotUpdateAnyEvent() {
        when(outboxEventRepository.findAllByStatusOrderByCreatedAt("PENDING", Limit.of(10))).thenReturn(List.of());

        outboxPublisher.publishEvents();

        verify(outboxEventRepository).findAllByStatusOrderByCreatedAt("PENDING", Limit.of(10));
        verify(outboxEventRepository, never()).updateStatus(anyLong(), anyString());
        verifyNoMoreInteractions(outboxEventRepository);
        verifyNoInteractions(rabbitTemplate);
    }

    @Test
    void publishEvents_whenBrokerSendFails_shouldLeaveEventPendingForNextAttempt() {
        OutboxEvent event = eventWithId(3L);
        event.setQueueName("stock.posted");
        when(outboxEventRepository.findAllByStatusOrderByCreatedAt("PENDING", Limit.of(10)))
                .thenReturn(List.of(event));
        doNothing().when(rabbitTemplate)
                .convertAndSend("notification.queue", messageFor(event));
        doThrow(new IllegalStateException("Broker unavailable"))
                .doNothing()
                .when(rabbitTemplate)
                .convertAndSend("stock.posted", messageFor(event));

        assertThatThrownBy(() -> outboxPublisher.publishEvents())
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Broker unavailable");
        verify(outboxEventRepository, never()).updateStatus(3L, "PUBLISHED");

        outboxPublisher.publishEvents();

        verify(rabbitTemplate, times(2))
                .convertAndSend("stock.posted", messageFor(event));
        verify(rabbitTemplate, times(2))
                .convertAndSend("notification.queue", messageFor(event));
        verify(outboxEventRepository).updateStatus(3L, "PUBLISHED");
    }

    @Test
    void eventResponse_shouldExposeSuccessAndMessage() {
        EventResponse response = new EventResponse(true, "Published");

        org.assertj.core.api.Assertions.assertThat(response.success()).isTrue();
        org.assertj.core.api.Assertions.assertThat(response.message()).isEqualTo("Published");
    }

    private EventMessage messageFor(OutboxEvent event) {
        return new EventMessage(
                event.getId(),
                event.getEventType(),
                event.getAggregateType(),
                event.getAggregateId(),
                event.getAggregateVersion(),
                event.getPayload());
    }

    private OutboxEvent eventWithId(Long id) {
        OutboxEvent event = new OutboxEvent();
        event.setId(id);
        event.setStatus("PENDING");
        return event;
    }
}
