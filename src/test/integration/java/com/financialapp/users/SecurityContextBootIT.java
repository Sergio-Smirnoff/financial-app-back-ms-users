package com.financialapp.users;

import com.financialapp.users.infrastructure.config.JwtProperties;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@EmbeddedKafka(partitions = 1, topics = "user.registered")
class SecurityContextBootIT {

    @Autowired SecurityFilterChain securityFilterChain;
    @Autowired PasswordEncoder passwordEncoder;
    @Autowired JwtProperties jwtProperties;

    @Test
    void buildsTheSecurityFilterChainBean() {
        // Given the full application context boots
        // When the security configuration is wired / Then the filter chain exists with filters
        assertThat(securityFilterChain).isNotNull();
        assertThat(securityFilterChain.getFilters()).isNotEmpty();
    }

    @Test
    void exposesABcryptPasswordEncoder() {
        // Given the context / Then the password encoder bean is BCrypt
        assertThat(passwordEncoder).isInstanceOf(BCryptPasswordEncoder.class);
    }

    @Test
    void bindsJwtPropertiesFromConfiguration() {
        // Given the test profile / Then JWT properties are bound from yaml
        assertThat(jwtProperties.getSecret()).isEqualTo("test-secret-key-that-is-long-enough-for-hs256-signing");
        assertThat(jwtProperties.getExpiration()).isEqualTo(3600000L);
        assertThat(jwtProperties.getRefreshExpiration()).isEqualTo(86400000L);
    }
}
