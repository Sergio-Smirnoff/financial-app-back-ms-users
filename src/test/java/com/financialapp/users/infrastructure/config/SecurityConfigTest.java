package com.financialapp.users.infrastructure.config;

import jakarta.servlet.Filter;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.StaticApplicationContext;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.ObjectPostProcessor;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.SecurityFilterChain;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class SecurityConfigTest {

    private final SecurityConfig config = new SecurityConfig();

    @Test
    void passwordEncoder_returnsBCryptPasswordEncoder() {
        PasswordEncoder encoder = config.passwordEncoder();
        assertThat(encoder).isInstanceOf(BCryptPasswordEncoder.class);
    }

    @Test
    void passwordEncoder_encodesAndMatchesPassword() {
        PasswordEncoder encoder = config.passwordEncoder();
        String encoded = encoder.encode("secret");
        assertThat(encoder.matches("secret", encoded)).isTrue();
        assertThat(encoder.matches("wrong", encoded)).isFalse();
    }

    @Test
    void securityFilterChain_buildsChainWithCsrfCookieFilter() throws Exception {
        HttpSecurity http = newHttpSecurity();

        SecurityFilterChain chain = config.securityFilterChain(http);

        assertThat(chain).isNotNull();
        List<Filter> filters = ((DefaultSecurityFilterChain) chain).getFilters();
        assertThat(filters).anyMatch(CsrfCookieFilter.class::isInstance);
    }

    private HttpSecurity newHttpSecurity() {
        ObjectPostProcessor<Object> objectPostProcessor = new ObjectPostProcessor<>() {
            @Override
            public <O> O postProcess(O object) {
                return object;
            }
        };
        AuthenticationManagerBuilder authenticationManagerBuilder =
                new AuthenticationManagerBuilder(objectPostProcessor);
        StaticApplicationContext applicationContext = new StaticApplicationContext();
        Map<Class<?>, Object> sharedObjects = new HashMap<>();
        sharedObjects.put(AuthenticationManager.class, mock(AuthenticationManager.class));
        sharedObjects.put(ApplicationContext.class, applicationContext);
        return new HttpSecurity(objectPostProcessor, authenticationManagerBuilder, sharedObjects);
    }
}
