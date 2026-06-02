package com.financialapp.users.web.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.financialapp.users.domain.model.User;
import com.financialapp.users.domain.model.valueObject.UserId;
import com.financialapp.users.infrastructure.persistence.entity.UserJpaEntity;
import com.financialapp.users.infrastructure.persistence.jpa.UserJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@EmbeddedKafka(partitions = 1, topics = "user.registered")
class AuthControllerIT {

    private static final String INTERNAL_HEADER = "X-Internal-Token";
    private static final String INTERNAL_TOKEN = "test-token";

    @Autowired MockMvc mvc;
    @Autowired UserJpaRepository userJpaRepository;
    @Autowired PasswordEncoder passwordEncoder;
    @Autowired ObjectMapper objectMapper;

    @BeforeEach
    void cleanDatabase() {
        userJpaRepository.deleteAll();
    }

    private UserJpaEntity seedUser(String email, String rawPassword) {
        UserJpaEntity entity = UserJpaEntity.builder()
                .email(email)
                .password(passwordEncoder.encode(rawPassword))
                .firstName("Ada")
                .lastName("Lovelace")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        return userJpaRepository.save(entity);
    }

    @Test
    void register_createsUser_andSetsAuthCookies() throws Exception {
        // Given a valid registration request
        String body = """
                {"email":"new@example.com","password":"password123","firstName":"Grace","lastName":"Hopper"}
                """;

        // When POSTing to /register
        MvcResult result = mvc.perform(post("/api/v1/auth/register")
                        .header(INTERNAL_HEADER, INTERNAL_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                // Then a 201 with the auth payload and three cookies is returned
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("User registered successfully"))
                .andExpect(jsonPath("$.data.email").value("new@example.com"))
                .andExpect(jsonPath("$.data.firstName").value("Grace"))
                .andExpect(cookie().exists("access_token"))
                .andExpect(cookie().exists("refresh_token"))
                .andExpect(cookie().exists("user_info"))
                .andReturn();

        assertThat(userJpaRepository.findByEmail("new@example.com")).isPresent();
        assertThat(result.getResponse().getHeaders("Set-Cookie"))
                .anyMatch(c -> c.startsWith("access_token="))
                .anyMatch(c -> c.startsWith("refresh_token="))
                .anyMatch(c -> c.startsWith("user_info="));
    }

    @Test
    void register_returnsConflict_whenEmailAlreadyExists() throws Exception {
        // Given an already-registered email
        seedUser("dup@example.com", "password123");
        String body = """
                {"email":"dup@example.com","password":"password123","firstName":"Grace","lastName":"Hopper"}
                """;

        // When registering again with the same email / Then 409 Conflict
        mvc.perform(post("/api/v1/auth/register")
                        .header(INTERNAL_HEADER, INTERNAL_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Email already registered: dup@example.com"));
    }

    @Test
    void register_returnsBadRequest_whenValidationFails() throws Exception {
        // Given a request with a bad email and short password
        String body = """
                {"email":"not-an-email","password":"short","firstName":"","lastName":"Hopper"}
                """;

        // When registering / Then 400 with validation errors
        mvc.perform(post("/api/v1/auth/register")
                        .header(INTERNAL_HEADER, INTERNAL_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.errors").isArray());
    }

    @Test
    void login_returnsOk_andSetsCookies_withValidCredentials() throws Exception {
        // Given an existing user
        seedUser("login@example.com", "password123");
        String body = """
                {"email":"login@example.com","password":"password123"}
                """;

        // When logging in / Then 200 with auth cookies
        mvc.perform(post("/api/v1/auth/login")
                        .header(INTERNAL_HEADER, INTERNAL_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Login successful"))
                .andExpect(jsonPath("$.data.email").value("login@example.com"))
                .andExpect(cookie().exists("access_token"))
                .andExpect(cookie().exists("refresh_token"))
                .andExpect(cookie().exists("user_info"));
    }

    @Test
    void login_returnsUnauthorized_whenPasswordWrong() throws Exception {
        // Given an existing user
        seedUser("login@example.com", "password123");
        String body = """
                {"email":"login@example.com","password":"wrongpassword"}
                """;

        // When logging in with a wrong password / Then 401
        mvc.perform(post("/api/v1/auth/login")
                        .header(INTERNAL_HEADER, INTERNAL_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Invalid email or password"));
    }

    @Test
    void login_returnsUnauthorized_whenUserUnknown() throws Exception {
        // Given no user with that email
        String body = """
                {"email":"ghost@example.com","password":"password123"}
                """;

        // When logging in / Then 401
        mvc.perform(post("/api/v1/auth/login")
                        .header(INTERNAL_HEADER, INTERNAL_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void refresh_returnsOk_andRotatesTokens_withValidRefreshCookie() throws Exception {
        // Given a registered user whose refresh token we obtain via login
        seedUser("refresh@example.com", "password123");
        String loginBody = """
                {"email":"refresh@example.com","password":"password123"}
                """;
        MvcResult login = mvc.perform(post("/api/v1/auth/login")
                        .header(INTERNAL_HEADER, INTERNAL_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginBody))
                .andExpect(status().isOk())
                .andReturn();
        String refreshToken = login.getResponse().getCookie("refresh_token").getValue();

        // When refreshing with that cookie / Then 200 with fresh cookies
        mvc.perform(post("/api/v1/auth/refresh")
                        .header(INTERNAL_HEADER, INTERNAL_TOKEN)
                        .cookie(new jakarta.servlet.http.Cookie("refresh_token", refreshToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Token refreshed"))
                .andExpect(cookie().exists("access_token"));
    }

    @Test
    void refresh_returnsUnauthorized_whenCookieMissing() throws Exception {
        // Given no refresh_token cookie / When refreshing / Then 401 (MissingRequestCookie)
        mvc.perform(post("/api/v1/auth/refresh")
                        .header(INTERNAL_HEADER, INTERNAL_TOKEN))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Authentication required"));
    }

    @Test
    void refresh_returnsUnauthorized_whenTokenInvalid() throws Exception {
        // Given a malformed refresh token / When refreshing / Then 401 (JwtException)
        mvc.perform(post("/api/v1/auth/refresh")
                        .header(INTERNAL_HEADER, INTERNAL_TOKEN)
                        .cookie(new jakarta.servlet.http.Cookie("refresh_token", "not-a-jwt")))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Invalid or expired token"));
    }

    @Test
    void refresh_returnsNotFound_whenUserNoLongerExists() throws Exception {
        // Given a user who logs in then is deleted
        UserJpaEntity entity = seedUser("vanish@example.com", "password123");
        String loginBody = """
                {"email":"vanish@example.com","password":"password123"}
                """;
        MvcResult login = mvc.perform(post("/api/v1/auth/login")
                        .header(INTERNAL_HEADER, INTERNAL_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginBody))
                .andReturn();
        String refreshToken = login.getResponse().getCookie("refresh_token").getValue();
        userJpaRepository.deleteById(entity.getId());

        // When refreshing / Then 404 because the user is gone
        mvc.perform(post("/api/v1/auth/refresh")
                        .header(INTERNAL_HEADER, INTERNAL_TOKEN)
                        .cookie(new jakarta.servlet.http.Cookie("refresh_token", refreshToken)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("User not found"));
    }

    @Test
    void logout_returnsOk_andExpiresAllCookies() throws Exception {
        // Given any caller / When POSTing to /logout / Then 200 with three expiring cookies
        MvcResult result = mvc.perform(post("/api/v1/auth/logout")
                        .header(INTERNAL_HEADER, INTERNAL_TOKEN))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Logged out successfully"))
                .andExpect(header().exists("Set-Cookie"))
                .andReturn();

        assertThat(result.getResponse().getHeaders("Set-Cookie"))
                .filteredOn(c -> c.startsWith("access_token=") || c.startsWith("refresh_token=")
                        || c.startsWith("user_info="))
                .hasSize(3)
                .allMatch(c -> c.contains("Max-Age=0"));
    }

    @Test
    void registeredUser_canSubsequentlyLogIn() throws Exception {
        // Given a freshly registered user
        String registerBody = """
                {"email":"roundtrip@example.com","password":"password123","firstName":"Grace","lastName":"Hopper"}
                """;
        mvc.perform(post("/api/v1/auth/register")
                        .header(INTERNAL_HEADER, INTERNAL_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registerBody))
                .andExpect(status().isCreated());

        // When logging in with the same credentials / Then it succeeds and returns the same id
        MvcResult login = mvc.perform(post("/api/v1/auth/login")
                        .header(INTERNAL_HEADER, INTERNAL_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email":"roundtrip@example.com","password":"password123"}
                                """))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode node = objectMapper.readTree(login.getResponse().getContentAsString());
        Long id = node.path("data").path("userId").asLong();
        User saved = new User(new UserId(id), "roundtrip@example.com", null, "Grace", "Hopper", null, null);
        assertThat(saved.id().value()).isEqualTo(id);
    }
}
