package com.financialapp.users.web.controller;

import com.financialapp.commons.core.response.ApiResponse;
import com.financialapp.users.domain.model.Session;
import com.financialapp.users.domain.model.User;
import com.financialapp.users.domain.model.valueObject.UserId;
import com.financialapp.users.domain.usecase.AuthenticateUserUseCase;
import com.financialapp.users.domain.usecase.RefreshSessionUseCase;
import com.financialapp.users.domain.usecase.RegisterUserUseCase;
import com.financialapp.users.domain.usecase.command.AuthenticateUserCommand;
import com.financialapp.users.domain.usecase.command.RefreshSessionCommand;
import com.financialapp.users.domain.usecase.command.RegisterUserCommand;
import com.financialapp.users.web.CookieService;
import com.financialapp.users.web.dto.request.LoginRequest;
import com.financialapp.users.web.dto.request.RegisterRequest;
import com.financialapp.users.web.dto.response.AuthResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AuthControllerTest {

    private RegisterUserUseCase registerUseCase;
    private AuthenticateUserUseCase authenticateUseCase;
    private RefreshSessionUseCase refreshSessionUseCase;
    private CookieService cookieService;
    private AuthController controller;

    @BeforeEach
    void setUp() {
        registerUseCase = mock(RegisterUserUseCase.class);
        authenticateUseCase = mock(AuthenticateUserUseCase.class);
        refreshSessionUseCase = mock(RefreshSessionUseCase.class);
        cookieService = mock(CookieService.class);
        controller = new AuthController(registerUseCase, authenticateUseCase, refreshSessionUseCase, cookieService);
    }

    private Session session() {
        User user = new User(new UserId(7L), "jane@doe.com", "hashed", "Jane", "Doe",
                LocalDateTime.now(), LocalDateTime.now());
        return new Session(user, "access-tok", "refresh-tok");
    }

    private void stubAuthCookies() {
        when(cookieService.createAccessTokenCookie("access-tok"))
                .thenReturn(ResponseCookie.from("access_token", "access-tok").path("/api").build());
        when(cookieService.createRefreshTokenCookie("refresh-tok"))
                .thenReturn(ResponseCookie.from("refresh_token", "refresh-tok").path("/api/v1/auth/refresh").build());
        when(cookieService.createUserInfoCookie(any(Session.class)))
                .thenReturn(ResponseCookie.from("user_info", "7|jane@doe.com|Jane+Doe").path("/").build());
    }

    @Test
    void register_returnsCreatedWithAuthCookiesAndBody() {
        Session session = session();
        RegisterRequest request = new RegisterRequest("jane@doe.com", "password1", "Jane", "Doe");
        when(registerUseCase.execute(any(RegisterUserCommand.class))).thenReturn(session);
        stubAuthCookies();

        ResponseEntity<ApiResponse<AuthResponse>> response = controller.register(request);

        ArgumentCaptor<RegisterUserCommand> captor = ArgumentCaptor.forClass(RegisterUserCommand.class);
        verify(registerUseCase).execute(captor.capture());
        RegisterUserCommand command = captor.getValue();
        assertThat(command.email()).isEqualTo("jane@doe.com");
        assertThat(command.password()).isEqualTo("password1");
        assertThat(command.firstName()).isEqualTo("Jane");
        assertThat(command.lastName()).isEqualTo("Doe");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody().getStatus()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.getBody().getMessage()).isEqualTo("User registered successfully");

        AuthResponse body = response.getBody().getData();
        assertThat(body.getUserId()).isEqualTo(7L);
        assertThat(body.getEmail()).isEqualTo("jane@doe.com");
        assertThat(body.getFirstName()).isEqualTo("Jane");
        assertThat(body.getLastName()).isEqualTo("Doe");

        List<String> setCookies = response.getHeaders().get(HttpHeaders.SET_COOKIE);
        assertThat(setCookies).hasSize(3);
        assertThat(setCookies).anyMatch(c -> c.startsWith("access_token="));
        assertThat(setCookies).anyMatch(c -> c.startsWith("refresh_token="));
        assertThat(setCookies).anyMatch(c -> c.startsWith("user_info="));
    }

    @Test
    void login_returnsOkWithAuthCookiesAndBody() {
        Session session = session();
        LoginRequest request = new LoginRequest("jane@doe.com", "password1");
        when(authenticateUseCase.execute(any(AuthenticateUserCommand.class))).thenReturn(session);
        stubAuthCookies();

        ResponseEntity<ApiResponse<AuthResponse>> response = controller.login(request);

        ArgumentCaptor<AuthenticateUserCommand> captor = ArgumentCaptor.forClass(AuthenticateUserCommand.class);
        verify(authenticateUseCase).execute(captor.capture());
        assertThat(captor.getValue().email()).isEqualTo("jane@doe.com");
        assertThat(captor.getValue().password()).isEqualTo("password1");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getBody().getMessage()).isEqualTo("Login successful");
        assertThat(response.getBody().getData().getUserId()).isEqualTo(7L);
        assertThat(response.getHeaders().get(HttpHeaders.SET_COOKIE)).hasSize(3);
    }

    @Test
    void refresh_returnsOkWithAuthCookiesAndBody() {
        Session session = session();
        when(refreshSessionUseCase.execute(any(RefreshSessionCommand.class))).thenReturn(session);
        stubAuthCookies();

        ResponseEntity<ApiResponse<AuthResponse>> response = controller.refresh("refresh-tok-input");

        ArgumentCaptor<RefreshSessionCommand> captor = ArgumentCaptor.forClass(RefreshSessionCommand.class);
        verify(refreshSessionUseCase).execute(captor.capture());
        assertThat(captor.getValue().refreshAuthentication()).isEqualTo("refresh-tok-input");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getMessage()).isEqualTo("Token refreshed");
        assertThat(response.getBody().getData().getEmail()).isEqualTo("jane@doe.com");
        assertThat(response.getHeaders().get(HttpHeaders.SET_COOKIE)).hasSize(3);
    }

    @Test
    void logout_returnsOkWithClearedCookies() {
        when(cookieService.createLogoutCookies()).thenReturn(List.of(
                ResponseCookie.from("access_token", "").path("/api").maxAge(0).build(),
                ResponseCookie.from("refresh_token", "").path("/api/v1/auth/refresh").maxAge(0).build(),
                ResponseCookie.from("user_info", "").path("/").maxAge(0).build()
        ));

        ResponseEntity<ApiResponse<Void>> response = controller.logout();

        verify(cookieService).createLogoutCookies();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getMessage()).isEqualTo("Logged out successfully");
        assertThat(response.getBody().getData()).isNull();

        List<String> setCookies = response.getHeaders().get(HttpHeaders.SET_COOKIE);
        assertThat(setCookies).hasSize(3);
        assertThat(setCookies).allMatch(c -> c.contains("Max-Age=0"));
    }
}
