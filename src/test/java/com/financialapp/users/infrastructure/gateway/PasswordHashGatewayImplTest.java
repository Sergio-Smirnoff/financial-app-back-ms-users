package com.financialapp.users.infrastructure.gateway;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PasswordHashGatewayImplTest {

    @Mock PasswordEncoder passwordEncoder;
    @InjectMocks PasswordHashGatewayImpl gateway;

    @Test
    void hash_delegatesToEncoder() {
        when(passwordEncoder.encode("raw")).thenReturn("hashed");
        assertThat(gateway.hash("raw")).isEqualTo("hashed");
        verify(passwordEncoder).encode("raw");
    }

    @Test
    void matches_returnsTrue_whenPasswordMatches() {
        when(passwordEncoder.matches("raw", "hashed")).thenReturn(true);
        assertThat(gateway.matches("raw", "hashed")).isTrue();
    }

    @Test
    void matches_returnsFalse_whenPasswordDoesNotMatch() {
        when(passwordEncoder.matches("wrong", "hashed")).thenReturn(false);
        assertThat(gateway.matches("wrong", "hashed")).isFalse();
    }
}
