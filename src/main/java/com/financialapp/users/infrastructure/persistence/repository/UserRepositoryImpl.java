package com.financialapp.users.infrastructure.persistence.repository;

import com.financialapp.users.domain.model.User;
import com.financialapp.users.domain.model.valueObject.UserId;
import com.financialapp.users.domain.repository.UserRepository;
import com.financialapp.users.infrastructure.persistence.jpa.UserJpaRepository;
import com.financialapp.users.infrastructure.persistence.mapper.UserPersistenceMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {

    private final UserJpaRepository userJpaRepository;
    private final UserPersistenceMapper mapper;

    @Override
    public User save(User user) {
        return mapper.toDomain(userJpaRepository.save(mapper.toJpa(user)));
    }

    @Override
    public Optional<User> findById(UserId id) {
        return userJpaRepository.findById(id.value()).map(mapper::toDomain);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userJpaRepository.findByEmail(email).map(mapper::toDomain);
    }
}
