package com.financialapp.users.web;

import com.financialapp.users.domain.model.Session;
import com.financialapp.users.domain.model.User;
import com.financialapp.users.domain.model.valueObject.UserId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseCookie;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class CookieServiceTest {

    private CookieService cookieService;

    @BeforeEach
    void setUp() {
        cookieService = new CookieService(false, 86_400_000L, 604_800_000L);
    }

    private Session session() {
        User user = new User(new UserId(1L), "a@b.com", "hashed", "John", "Doe",
                LocalDateTime.now(), LocalDateTime.now());
        return new Session(user, "access-tok", "refresh-tok");
    }

    @Test
    void createAccessTokenCookie_hasCorrectAttributes() {
        ResponseCookie cookie = cookieService.createAccessTokenCookie("tok");
        assertThat(cookie.getName()).isEqualTo("access_token");
        assertThat(cookie.getValue()).isEqualTo("tok");
        assertThat(cookie.isHttpOnly()).isTrue();
        assertThat(cookie.getPath()).isEqualTo("/api");
        assertThat(cookie.getMaxAge()).isEqualTo(Duration.ofSeconds(86400));
    }

    @Test
    void createRefreshTokenCookie_hasCorrectAttributes() {
        ResponseCookie cookie = cookieService.createRefreshTokenCookie("rtok");
        assertThat(cookie.getName()).isEqualTo("refresh_token");
        assertThat(cookie.getValue()).isEqualTo("rtok");
        assertThat(cookie.isHttpOnly()).isTrue();
        assertThat(cookie.getPath()).isEqualTo("/api/v1/auth/refresh");
        assertThat(cookie.getMaxAge()).isEqualTo(Duration.ofSeconds(604800));
    }

    @Test
    void createUserInfoCookie_encodesUserData() {
        ResponseCookie cookie = cookieService.createUserInfoCookie(session());
        assertThat(cookie.getName()).isEqualTo("user_info");
        assertThat(cookie.isHttpOnly()).isFalse();
        assertThat(cookie.getPath()).isEqualTo("/");
        String decoded = URLDecoder.decode(cookie.getValue(), StandardCharsets.UTF_8);
        assertThat(decoded).isEqualTo("1|a@b.com|John+Doe");
    }

    @Test
    void createLogoutCookies_returnsThreeCookiesWithZeroMaxAge() {
        List<ResponseCookie> cookies = cookieService.createLogoutCookies();
        assertThat(cookies).hasSize(3);
        assertThat(cookies).allSatisfy(c -> assertThat(c.getMaxAge()).isEqualTo(Duration.ZERO));
        assertThat(cookies).extracting(ResponseCookie::getName)
                .containsExactlyInAnyOrder("access_token", "refresh_token", "user_info");
    }
}
