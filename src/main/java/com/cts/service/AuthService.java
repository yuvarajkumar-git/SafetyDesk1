package com.cts.service;

import com.cts.dto.request.LoginRequest;
import com.cts.dto.request.RefreshRequest;
import com.cts.dto.response.LoginResponse;

public interface AuthService {

    LoginResponse login(LoginRequest request);

    LoginResponse refresh(RefreshRequest request);
}