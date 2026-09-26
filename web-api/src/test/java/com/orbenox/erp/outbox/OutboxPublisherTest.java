package com.orbenox.erp.outbox;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

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
        when(outboxEventRepository.findAllByStatus("PENDING"))
                .thenReturn(List.of(firstEvent, secondEvent));

        outboxPublisher.publishEvents();

        verify(outboxEventRepository).findAllByStatus("PENDING");
        verify(outboxEventRepository).updateStatus(1L, "PUBLISHED");
        verify(outboxEventRepository).updateStatus(2L, "PUBLISHED");
        verify(rabbitTemplate).convertAndSend("outbox-exchange", "outbox-routing-key", firstEvent);
        verify(rabbitTemplate).convertAndSend("outbox-exchange", "outbox-routing-key", secondEvent);
        verifyNoMoreInteractions(outboxEventRepository);
        verifyNoMoreInteractions(rabbitTemplate);
    }

    @Test
    void publishEvents_whenNoPendingEventsExist_shouldNotUpdateAnyEvent() {
        when(outboxEventRepository.findAllByStatus("PENDING")).thenReturn(List.of());

        outboxPublisher.publishEvents();

        verify(outboxEventRepository).findAllByStatus("PENDING");
        verify(outboxEventRepository, never()).updateStatus(anyLong(), anyString());
        verifyNoMoreInteractions(outboxEventRepository);
        verifyNoInteractions(rabbitTemplate);
    }

    private OutboxEvent eventWithId(Long id) {
        OutboxEvent event = new OutboxEvent();
        event.setId(id);
        event.setStatus("PENDING");
        return event;
    }
}
