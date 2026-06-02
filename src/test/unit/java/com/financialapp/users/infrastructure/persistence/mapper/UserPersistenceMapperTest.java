package com.financialapp.users.infrastructure.persistence.mapper;

import com.financialapp.users.domain.model.User;
import com.financialapp.users.domain.model.valueObject.UserId;
import com.financialapp.users.infrastructure.persistence.entity.UserJpaEntity;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class UserPersistenceMapperTest {

    private final UserPersistenceMapper mapper = new UserPersistenceMapper();

    private static final LocalDateTime NOW = LocalDateTime.of(2024, 1, 1, 12, 0);

    @Test
    void toDomain_returnsNull_whenEntityIsNull() {
        assertThat(mapper.toDomain(null)).isNull();
    }

    @Test
    void toDomain_mapsAllFields() {
        UserJpaEntity entity = UserJpaEntity.builder()
                .id(5L).email("a@b.com").password("hashed")
                .firstName("John").lastName("Doe")
                .createdAt(NOW).updatedAt(NOW).build();

        User user = mapper.toDomain(entity);

        assertThat(user.id()).isEqualTo(new UserId(5L));
        assertThat(user.email()).isEqualTo("a@b.com");
        assertThat(user.password()).isEqualTo("hashed");
        assertThat(user.firstName()).isEqualTo("John");
        assertThat(user.lastName()).isEqualTo("Doe");
        assertThat(user.createdAt()).isEqualTo(NOW);
        assertThat(user.updatedAt()).isEqualTo(NOW);
    }

    @Test
    void toJpa_returnsNull_whenUserIsNull() {
        assertThat(mapper.toJpa(null)).isNull();
    }

    @Test
    void toJpa_mapsAllFields_includingId() {
        User user = new User(new UserId(3L), "a@b.com", "hashed", "Jane", "Doe", NOW, NOW);
        UserJpaEntity entity = mapper.toJpa(user);

        assertThat(entity.getId()).isEqualTo(3L);
        assertThat(entity.getEmail()).isEqualTo("a@b.com");
        assertThat(entity.getPassword()).isEqualTo("hashed");
        assertThat(entity.getFirstName()).isEqualTo("Jane");
        assertThat(entity.getLastName()).isEqualTo("Doe");
        assertThat(entity.getCreatedAt()).isEqualTo(NOW);
        assertThat(entity.getUpdatedAt()).isEqualTo(NOW);
    }

    @Test
    void toJpa_setsNullId_whenUserHasNoId() {
        User user = new User(null, "a@b.com", "hashed", "Jane", "Doe", NOW, NOW);
        assertThat(mapper.toJpa(user).getId()).isNull();
    }
}
