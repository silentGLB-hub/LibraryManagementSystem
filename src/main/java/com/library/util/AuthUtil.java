package com.library.util;

import com.library.model.User;
import jakarta.servlet.http.HttpSession;

import java.util.Arrays;

public final class AuthUtil {

    private AuthUtil() {
    }

    public static User currentUser(HttpSession session) {
        Object value = session == null ? null : session.getAttribute("user");
        return value instanceof User ? (User) value : null;
    }

    public static boolean isLoggedIn(HttpSession session) {
        return currentUser(session) != null;
    }

    public static boolean hasAnyRole(HttpSession session, String... roles) {
        User user = currentUser(session);
        if (user == null || user.getRole() == null) {
            return false;
        }
        return Arrays.stream(roles).anyMatch(role -> role.equalsIgnoreCase(user.getRole()));
    }

    public static boolean isAdmin(HttpSession session) {
        return hasAnyRole(session, "ADMIN");
    }

    public static boolean isLibrarian(HttpSession session) {
        return hasAnyRole(session, "LIBRARIAN");
    }

    public static boolean isReader(HttpSession session) {
        return hasAnyRole(session, "READER");
    }

    public static boolean canManageLibrary(HttpSession session) {
        return hasAnyRole(session, "ADMIN", "LIBRARIAN");
    }

    public static boolean canManageUsers(HttpSession session) {
        return isAdmin(session);
    }

    public static boolean canViewReports(HttpSession session) {
        return hasAnyRole(session, "ADMIN", "LIBRARIAN");
    }
}