package com.mobdeve.s18.banyoboyz.flushfinders.data;

public class AccountData {
    public enum AccountType
    {
        USER,
        MODERATOR,
        ADMINISTRATOR
    };

    private String name;
    private String email;
    private boolean isActive;
    private int profilePictureResource;

    private AccountType type;

    public AccountData(String name, String email, boolean isActive, int profilePictureResource, AccountType type) {
        this.name = name;
        this.email = email;
        this.isActive = isActive;
        this.profilePictureResource = profilePictureResource;
        this.type = type;
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

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public int getProfilePictureResource() {
        return profilePictureResource;
    }

    public void setProfilePictureResource(int profilePictureResource) {
        this.profilePictureResource = profilePictureResource;
    }

    public AccountType getType() {
        return type;
    }

    public void setType(AccountType type) {
        this.type = type;
    }

}
