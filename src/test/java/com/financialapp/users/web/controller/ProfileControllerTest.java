package com.financialapp.users.web.controller;

import com.financialapp.users.domain.gateway.AuthenticationProviderGateway;
import com.financialapp.users.domain.model.User;
import com.financialapp.users.domain.model.valueObject.UserId;
import com.financialapp.users.domain.usecase.GetUserProfileUseCase;
import com.financialapp.users.domain.usecase.UpdateUserPasswordUseCase;
import com.financialapp.users.domain.usecase.UpdateUserProfileUseCase;
import com.financialapp.users.domain.usecase.command.UpdateUserProfileCommand;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ProfileControllerTest {

    private GetUserProfileUseCase getUserProfile;
    private UpdateUserProfileUseCase updateProfileUseCase;
    private UpdateUserPasswordUseCase updatePasswordUseCase;
    private AuthenticationProviderGateway authProvider;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        getUserProfile = mock(GetUserProfileUseCase.class);
        updateProfileUseCase = mock(UpdateUserProfileUseCase.class);
        updatePasswordUseCase = mock(UpdateUserPasswordUseCase.class);
        authProvider = mock(AuthenticationProviderGateway.class);
        ProfileController controller = new ProfileController(
                getUserProfile, updateProfileUseCase, updatePasswordUseCase, authProvider);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    private User sampleUser() {
        return new User(
                new UserId(5L),
                "ana@example.com",
                "hashed",
                "Ana",
                "Silva",
                LocalDateTime.of(2026, 1, 15, 10, 0),
                LocalDateTime.of(2026, 1, 15, 10, 0)
        );
    }

    @Test
    void returnsProfileForAuthenticatedUser() throws Exception {
        when(getUserProfile.execute(new UserId(5L))).thenReturn(sampleUser());

        mockMvc.perform(get("/api/v1/users/me/profile").header("X-User-Id", 5L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.email").value("ana@example.com"))
                .andExpect(jsonPath("$.data.name").value("Ana Silva"));
    }

    @Test
    void updateProfile_returnsUpdatedProfile() throws Exception {
        User updated = sampleUser();
        when(updateProfileUseCase.execute(any(UpdateUserProfileCommand.class))).thenReturn(updated);

        mockMvc.perform(put("/api/v1/users/me/profile")
                        .header("X-User-Id", 5L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "firstName": "Ana",
                                  "lastName": "Silva"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.firstName").value("Ana"))
                .andExpect(jsonPath("$.data.lastName").value("Silva"));
    }

    @Test
    void updatePassword_returnsOk() throws Exception {
        mockMvc.perform(put("/api/v1/users/me/password")
                        .header("X-User-Id", 5L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "currentPassword": "oldPassword123",
                                  "newPassword": "newPassword123"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Password changed successfully"));
    }
}
