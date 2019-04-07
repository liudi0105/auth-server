package com.github.rudylucky.auth.dto;

public class UserStatusDTO {

    private String username;
    private Boolean locked;
    private Boolean expired;

    public UserStatusDTO(String username, Boolean locked, Boolean expired) {
        this.username = username;
        this.locked = locked;
        this.expired = expired;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Boolean getLocked() {
        return locked;
    }

    public void setLocked(Boolean locked) {
        this.locked = locked;
    }

    public Boolean getExpired() {
        return expired;
    }

    public void setExpired(Boolean expired) {
        this.expired = expired;
    }
}
