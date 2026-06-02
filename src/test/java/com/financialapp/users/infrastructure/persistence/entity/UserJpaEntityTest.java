package com.financialapp.users.infrastructure.persistence.entity;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UserJpaEntityTest {

    @Test
    void onCreate_setsCreatedAtAndUpdatedAt() {
        UserJpaEntity entity = new UserJpaEntity();
        entity.onCreate();
        assertThat(entity.getCreatedAt()).isNotNull();
        assertThat(entity.getUpdatedAt()).isNotNull();
    }

    @Test
    void onUpdate_updatesUpdatedAt() throws InterruptedException {
        UserJpaEntity entity = new UserJpaEntity();
        entity.onCreate();
        var createdAt = entity.getCreatedAt();
        Thread.sleep(1);
        entity.onUpdate();
        assertThat(entity.getUpdatedAt()).isAfterOrEqualTo(createdAt);
    }

    @Test
    void builder_setsAllFields() {
        UserJpaEntity entity = UserJpaEntity.builder()
                .id(1L)
                .email("a@b.com")
                .password("hashed")
                .firstName("John")
                .lastName("Doe")
                .build();
        assertThat(entity.getId()).isEqualTo(1L);
        assertThat(entity.getEmail()).isEqualTo("a@b.com");
        assertThat(entity.getPassword()).isEqualTo("hashed");
        assertThat(entity.getFirstName()).isEqualTo("John");
        assertThat(entity.getLastName()).isEqualTo("Doe");
    }
}
