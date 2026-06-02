package com.financialapp.users.domain.gateway;

public interface PasswordHashGateway {
    String hash(String password);
    boolean matches(String rawPassword, String hashedPassword);
}
