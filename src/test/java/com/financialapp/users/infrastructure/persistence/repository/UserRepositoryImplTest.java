package com.financialapp.users.infrastructure.persistence.repository;

import com.financialapp.users.domain.model.User;
import com.financialapp.users.domain.model.valueObject.UserId;
import com.financialapp.users.infrastructure.persistence.entity.UserJpaEntity;
import com.financialapp.users.infrastructure.persistence.jpa.UserJpaRepository;
import com.financialapp.users.infrastructure.persistence.mapper.UserPersistenceMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserRepositoryImplTest {

    @Mock UserJpaRepository userJpaRepository;
    @Mock UserPersistenceMapper mapper;

    @InjectMocks UserRepositoryImpl repository;

    private static final LocalDateTime NOW = LocalDateTime.of(2024, 1, 1, 0, 0);

    private User domainUser() {
        return new User(new UserId(1L), "a@b.com", "hashed", "John", "Doe", NOW, NOW);
    }

    private UserJpaEntity jpaEntity() {
        return UserJpaEntity.builder().id(1L).email("a@b.com")
                .password("hashed").firstName("John").lastName("Doe")
                .createdAt(NOW).updatedAt(NOW).build();
    }

    @Test
    void save_persistsAndReturnsMappedUser() {
        User input = domainUser();
        UserJpaEntity entity = jpaEntity();
        User expected = domainUser();
        when(mapper.toJpa(input)).thenReturn(entity);
        when(userJpaRepository.save(entity)).thenReturn(entity);
        when(mapper.toDomain(entity)).thenReturn(expected);

        User result = repository.save(input);

        assertThat(result).isEqualTo(expected);
    }

    @Test
    void findById_returnsMappedUser_whenFound() {
        UserId id = new UserId(1L);
        UserJpaEntity entity = jpaEntity();
        User expected = domainUser();
        when(userJpaRepository.findById(1L)).thenReturn(Optional.of(entity));
        when(mapper.toDomain(entity)).thenReturn(expected);

        Optional<User> result = repository.findById(id);

        assertThat(result).contains(expected);
    }

    @Test
    void findById_returnsEmpty_whenNotFound() {
        when(userJpaRepository.findById(99L)).thenReturn(Optional.empty());
        assertThat(repository.findById(new UserId(99L))).isEmpty();
    }

    @Test
    void findByEmail_returnsMappedUser_whenFound() {
        UserJpaEntity entity = jpaEntity();
        User expected = domainUser();
        when(userJpaRepository.findByEmail("a@b.com")).thenReturn(Optional.of(entity));
        when(mapper.toDomain(entity)).thenReturn(expected);

        Optional<User> result = repository.findByEmail("a@b.com");

        assertThat(result).contains(expected);
    }

    @Test
    void findByEmail_returnsEmpty_whenNotFound() {
        when(userJpaRepository.findByEmail("x@y.com")).thenReturn(Optional.empty());
        assertThat(repository.findByEmail("x@y.com")).isEmpty();
    }
}
