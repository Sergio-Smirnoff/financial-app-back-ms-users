package com.financialapp.users.infrastructure.config;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class JwtPropertiesTest {

    @Test
    void settersAndGetters_roundTrip() {
        JwtProperties props = new JwtProperties();
        props.setSecret("my-secret");
        props.setExpiration(3600000L);
        props.setRefreshExpiration(86400000L);

        assertThat(props.getSecret()).isEqualTo("my-secret");
        assertThat(props.getExpiration()).isEqualTo(3600000L);
        assertThat(props.getRefreshExpiration()).isEqualTo(86400000L);
    }
}
