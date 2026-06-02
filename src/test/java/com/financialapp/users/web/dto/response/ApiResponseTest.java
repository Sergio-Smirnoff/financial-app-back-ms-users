package com.financialapp.users.web.dto.response;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ApiResponseTest {

    @Test
    void ok_setsSuccessTrueAndData() {
        ApiResponse<String> response = ApiResponse.ok("done", "payload");
        assertThat(response.isSuccess()).isTrue();
        assertThat(response.getMessage()).isEqualTo("done");
        assertThat(response.getData()).isEqualTo("payload");
        assertThat(response.getTimestamp()).isNotNull();
    }

    @Test
    void error_withMessage_setsSuccessFalse() {
        ApiResponse<Void> response = ApiResponse.error("something went wrong");
        assertThat(response.isSuccess()).isFalse();
        assertThat(response.getMessage()).isEqualTo("something went wrong");
        assertThat(response.getData()).isNull();
    }

    @Test
    void error_withErrors_setsErrorList() {
        List<String> errors = List.of("field is required", "email is invalid");
        ApiResponse<Void> response = ApiResponse.error("Validation failed", errors);
        assertThat(response.isSuccess()).isFalse();
        assertThat(response.getErrors()).containsExactlyElementsOf(errors);
    }
}
