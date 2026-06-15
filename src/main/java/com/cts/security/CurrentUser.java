package com.cts.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.cts.entity.User;
import com.cts.enums.Role;
import com.cts.exception.AccessForbiddenException;

/**
 * Reads the authenticated user from the security context.
 * Services call this instead of touching Spring Security directly.
 */
@Component
public class CurrentUser {

    /** The full authenticated User entity. Throws 403 if somehow unauthenticated. */
    public User get() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof AppUserDetails details)) {
            throw new AccessForbiddenException("No authenticated user in context");
        }
        return details.getUser();
    }

    public Long id() {
        return get().getUserId();
    }

    public Role role() {
        return get().getRole();
    }

    public Long siteId() {
        return get().getSiteId();
    }

    /** True if the current user holds any of the given roles. */
    public boolean hasAnyRole(Role... roles) {
        Role mine = role();
        for (Role r : roles) {
            if (mine == r) {
                return true;
            }
        }
        return false;
    }
}