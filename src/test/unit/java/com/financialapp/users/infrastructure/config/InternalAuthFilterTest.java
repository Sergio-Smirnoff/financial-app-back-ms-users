package com.financialapp.users.infrastructure.config;

import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class InternalAuthFilterTest {

    private InternalAuthFilter filter;

    @BeforeEach
    void setUp() {
        filter = new InternalAuthFilter();
        ReflectionTestUtils.setField(filter, "internalToken", "valid-token");
    }

    @Test
    void doFilterInternal_allowsRequest_withCorrectToken() throws Exception {
        // Given a protected path with the correct internal token
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/api/v1/auth/login");
        request.addHeader("X-Internal-Token", "valid-token");
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain chain = mock(FilterChain.class);

        // When the filter runs
        filter.doFilterInternal(request, response, chain);

        // Then the request passes through
        verify(chain).doFilter(request, response);
        assertThat(response.getStatus()).isNotEqualTo(401);
    }

    @Test
    void doFilterInternal_blocks_whenTokenMissing() throws Exception {
        // Given a protected path with no internal token header
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/api/v1/auth/login");
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain chain = mock(FilterChain.class);

        // When the filter runs
        filter.doFilterInternal(request, response, chain);

        // Then it is blocked with 401 and the chain is never invoked
        assertThat(response.getStatus()).isEqualTo(401);
        verifyNoInteractions(chain);
    }

    @Test
    void doFilterInternal_blocks_whenTokenIsWrong() throws Exception {
        // Given a protected path with an incorrect internal token
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/api/v1/auth/login");
        request.addHeader("X-Internal-Token", "wrong-token");
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain chain = mock(FilterChain.class);

        // When the filter runs
        filter.doFilterInternal(request, response, chain);

        // Then it is blocked with 401
        assertThat(response.getStatus()).isEqualTo(401);
        verifyNoInteractions(chain);
    }

    @Test
    void doFilterInternal_blocks_whenConfiguredTokenIsEmpty() throws Exception {
        // Given the service is misconfigured with an empty internal token
        ReflectionTestUtils.setField(filter, "internalToken", "");
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/api/v1/auth/login");
        request.addHeader("X-Internal-Token", "");
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain chain = mock(FilterChain.class);

        // When the filter runs
        filter.doFilterInternal(request, response, chain);

        // Then every request is blocked regardless of the supplied header
        assertThat(response.getStatus()).isEqualTo(401);
        verifyNoInteractions(chain);
    }

    @Test
    void doFilterInternal_blocks_whenConfiguredTokenIsNull() throws Exception {
        // Given the service is misconfigured with no internal token at all
        ReflectionTestUtils.setField(filter, "internalToken", null);
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/api/v1/auth/login");
        request.addHeader("X-Internal-Token", "anything");
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain chain = mock(FilterChain.class);

        // When the filter runs
        filter.doFilterInternal(request, response, chain);

        // Then every request is blocked
        assertThat(response.getStatus()).isEqualTo(401);
        verifyNoInteractions(chain);
    }

    @ParameterizedTest
    @ValueSource(strings = {"/actuator/health", "/swagger-ui/index.html", "/v3/api-docs/users"})
    void doFilterInternal_skipsTokenCheck_forPublicPaths(String path) throws Exception {
        // Given a public path that bypasses the internal-token check
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI(path);
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain chain = mock(FilterChain.class);

        // When the filter runs / Then the request passes through unchecked
        filter.doFilterInternal(request, response, chain);

        verify(chain).doFilter(request, response);
    }
}
