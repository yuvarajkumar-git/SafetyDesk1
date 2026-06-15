package com.cts.security;

import java.io.IOException;
import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import com.cts.service.AuditLogService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import lombok.RequiredArgsConstructor;

/**
 * Returns a JSON 403 body (Story 51) and audit-logs the forbidden access
 * with the authenticated user's id. Writes the small fixed JSON shape directly
 * to avoid an ObjectMapper dependency in the security layer.
 */
@Component
@RequiredArgsConstructor
public class RestAccessDeniedHandler implements AccessDeniedHandler {

    private final AuditLogService auditLogService;

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException {

        // Story 51: forbidden access is audit-logged (we know the authenticated user here)
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof AppUserDetails principal) {
            auditLogService.record(
                    principal.getUser().getUserId(),
                    "ACCESS_DENIED_" + request.getMethod() + "_" + request.getRequestURI(),
                    "Security", principal.getUser().getUserId());
        }

        response.setStatus(HttpStatus.FORBIDDEN.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        // Mirror the ApiResponse shape { success, message, data, timestamp }
        String json = "{"
                + "\"success\":false,"
                + "\"message\":\"Access denied: your role does not permit this action\","
                + "\"data\":null,"
                + "\"timestamp\":\"" + LocalDateTime.now() + "\""
                + "}";
        response.getWriter().write(json);
    }
}