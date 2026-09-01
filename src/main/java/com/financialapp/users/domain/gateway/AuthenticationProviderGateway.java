package com.financialapp.users.domain.gateway;

import com.financialapp.users.domain.model.User;
import com.financialapp.users.domain.model.valueObject.RefreshTokenClaims;
import com.financialapp.users.domain.model.valueObject.RefreshTokenId;
import com.financialapp.users.domain.model.valueObject.SessionId;
import com.financialapp.users.domain.model.valueObject.UserId;

public interface AuthenticationProviderGateway {
    String generateAuthenticationToken(User user, SessionId sessionId);
    String refreshAuthenticationToken(User user, RefreshTokenId jti, boolean rememberMe);
    UserId getUserIdFromAccessToken(String token);
    Long getSessionIdFromAccessToken(String token);
    RefreshTokenClaims getRefreshTokenClaims(String token);
}
