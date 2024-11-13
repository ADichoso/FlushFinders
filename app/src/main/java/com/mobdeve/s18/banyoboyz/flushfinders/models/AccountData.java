package com.mobdeve.s18.banyoboyz.flushfinders.models;

import java.time.Instant;

public class AccountData {
    public enum AccountType
    {
        USER,
        MODERATOR,
        ADMINISTRATOR
    };

    private String name;
    private String email;
    private String password;
    private boolean isActive;
    private String profilePicture;
    private Instant creationTime;
    private AccountType type;

    public AccountData(){}

    public AccountData(String email, String name, String password, boolean isActive, String profilePicture, Instant creationTime, AccountType type) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.isActive = isActive;
        this.profilePicture = profilePicture;
        this.creationTime = creationTime;
        this.type = type;
    }

    public static AccountType convertType(String type)
    {
        switch (type) {
            case "USER":
                return AccountType.USER;
            case "MODERATOR":
                return AccountType.MODERATOR;
            case "ADMINISTRATOR":
                return AccountType.ADMINISTRATOR;
        }

        return null;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }

    public Instant getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(Instant creationTime) {
        this.creationTime = creationTime;
    }

    public AccountType getType() {
        return type;
    }

    public void setType(AccountType type) {
        this.type = type;
    }

}
