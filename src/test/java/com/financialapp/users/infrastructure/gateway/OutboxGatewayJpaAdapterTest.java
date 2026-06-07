package com.financialapp.users.infrastructure.gateway;

import com.financialapp.commons.messaging.domain.model.EventId;
import com.financialapp.commons.messaging.domain.model.EventType;
import com.financialapp.commons.messaging.domain.model.OutboxRecord;
import com.financialapp.users.infrastructure.persistence.entity.OutboxEventEntity;
import com.financialapp.users.infrastructure.persistence.jpa.OutboxEventJpaRepository;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.data.domain.Limit;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class OutboxGatewayJpaAdapterTest {

    private final OutboxEventJpaRepository repo = mock(OutboxEventJpaRepository.class);
    private final OutboxGatewayJpaAdapter adapter = new OutboxGatewayJpaAdapter(repo);

    @Test
    void savesRecordAsEntityWithCorrectFields() {
        OutboxRecord record = OutboxRecord.create(
                "users.user.registered", "42",
                new EventType("users.user.registered"),
                "ms-users",
                "https://schemas.financial-app/users/user-registered/v1",
                "{\"email\":\"a@b.c\"}");

        adapter.save(record);

        ArgumentCaptor<OutboxEventEntity> captor = ArgumentCaptor.forClass(OutboxEventEntity.class);
        verify(repo).save(captor.capture());
        OutboxEventEntity saved = captor.getValue();
        assertThat(saved.getEventId()).isEqualTo(record.eventId().value());
        assertThat(saved.getTopic()).isEqualTo("users.user.registered");
        assertThat(saved.getAggregateKey()).isEqualTo("42");
        assertThat(saved.getCeType()).isEqualTo("users.user.registered");
        assertThat(saved.getCeSource()).isEqualTo("ms-users");
        assertThat(saved.isSent()).isFalse();
    }

    @Test
    void findUnsentRebuildsOutboxRecordsFromEntities() {
        OutboxEventEntity entity = new OutboxEventEntity();
        entity.setEventId("evt-1");
        entity.setTopic("users.user.registered");
        entity.setAggregateKey("1");
        entity.setCeType("users.user.registered");
        entity.setCeSource("ms-users");
        entity.setDataSchema("https://schemas.financial-app/users/user-registered/v1");
        entity.setDataJson("{\"email\":\"x@y.z\"}");
        entity.setSent(false);

        when(repo.findBySentFalseOrderByIdAsc(any(Limit.class))).thenReturn(List.of(entity));

        List<OutboxRecord> result = adapter.findUnsent(10);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).eventId().value()).isEqualTo("evt-1");
        assertThat(result.get(0).topic()).isEqualTo("users.user.registered");
        assertThat(result.get(0).key()).isEqualTo("1");
    }

    @Test
    void markSentSetsEntityAsSent() {
        OutboxEventEntity entity = new OutboxEventEntity();
        entity.setEventId("evt-2");
        entity.setSent(false);

        when(repo.findByEventId("evt-2")).thenReturn(Optional.of(entity));

        adapter.markSent(new EventId("evt-2"));

        assertThat(entity.isSent()).isTrue();
        assertThat(entity.getSentAt()).isNotNull();
        verify(repo).save(entity);
    }
}
