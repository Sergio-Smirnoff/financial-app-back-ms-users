package com.financialapp.users.domain.repository;

import com.financialapp.users.domain.model.User;
import com.financialapp.users.domain.model.valueObject.UserId;

import java.util.Optional;

public interface UserRepository {
    User save(User user);
    Optional<User> findById(UserId id);
    Optional<User> findByEmail(String email);
}
