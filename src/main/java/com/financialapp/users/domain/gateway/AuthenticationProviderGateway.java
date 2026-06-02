package com.financialapp.users.domain.gateway;

import com.financialapp.users.domain.model.User;
import com.financialapp.users.domain.model.valueObject.UserId;

public interface AuthenticationProviderGateway {
    String generateAuthenticationToken(User user);
    String refreshAuthenticationToken(User user);
    UserId getUserId(String token);
}
