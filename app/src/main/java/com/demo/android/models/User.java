package com.demo.android.models;

import com.demo.android.helpers.PrefsHelper;
import com.demo.android.helpers.RoleHelper;

public class User {
    private String login;
    private int id;

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    private Role role;

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
