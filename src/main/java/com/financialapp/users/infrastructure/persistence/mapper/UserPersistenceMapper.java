package com.financialapp.users.infrastructure.persistence.mapper;

import com.financialapp.users.domain.model.User;
import com.financialapp.users.domain.model.valueObject.UserId;
import com.financialapp.users.infrastructure.persistence.entity.UserJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class UserPersistenceMapper {

    public User toDomain(UserJpaEntity entity) {
        if (entity == null) return null;
        return new User(
                new UserId(entity.getId()),
                entity.getEmail(),
                entity.getPassword(),
                entity.getFirstName(),
                entity.getLastName(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    public UserJpaEntity toJpa(User user) {
        if (user == null) return null;
        return UserJpaEntity.builder()
                .id(user.id() != null ? user.id().value() : null)
                .email(user.email())
                .password(user.password())
                .firstName(user.firstName())
                .lastName(user.lastName())
                .createdAt(user.createdAt())
                .updatedAt(user.updatedAt())
                .build();
    }
}
