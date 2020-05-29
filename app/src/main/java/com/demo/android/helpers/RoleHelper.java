package com.demo.android.helpers;

public class RoleHelper {
    public static final String ROLE_ADMIN = "Администратор";
    public static final String ROLE_MODERATOR = "Модератор";
    public static final String ROLE_MANAGER = "Менеджер";

    public static boolean isAdmin() {
        return PrefsHelper.getRole().equals(RoleHelper.ROLE_ADMIN);
    }

    public static boolean isModerator() {
        return PrefsHelper.getRole().equals(RoleHelper.ROLE_MODERATOR);
    }

    public static boolean isManager() {
        return PrefsHelper.getRole().equals(RoleHelper.ROLE_MANAGER);
    }
}
