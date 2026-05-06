package com.campusevent.config;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

public class CustomUserDetails extends User {

    private int points;
    private final String name;

    public CustomUserDetails(String username, String password, Collection<? extends GrantedAuthority> authorities, int points, String name) {
        super(username, password, authorities);
        this.points = points;
        this.name = name;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public int getPoints() {
        return points;
    }

    public String getName() {
        return name;
    }
}
