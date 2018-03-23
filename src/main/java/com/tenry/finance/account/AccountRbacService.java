package com.tenry.finance.account;

import org.springframework.security.core.Authentication;

import javax.servlet.http.HttpServletRequest;

public interface AccountRbacService {
    boolean hasPermission(HttpServletRequest request, Authentication authentication);
}
