package com.mobdeve.s18.banyoboyz.flushfinders.models;

import java.time.Instant;
import java.util.ArrayList;

public class AccountData
{
    public enum AccountType
    {
        USER,
        MODERATOR,
        ADMINISTRATOR
    };

    private String name;
    private String email;
    private boolean isActive;
    private String profilePicture;
    private Instant creationTime;
    private AccountType type;
    private boolean isMe;
    private ArrayList<RestroomData> favorite_restrooms;

    public AccountData(){}

    public AccountData(String email,
                       String name,
                       boolean isActive,
                       String profilePicture,
                       Instant creationTime,
                       AccountType type,
                       boolean isMe,
                       ArrayList<RestroomData> favorite_restrooms)
    {
        this.name = name;
        this.email = email;
        this.isActive = isActive;
        this.profilePicture = profilePicture;
        this.creationTime = creationTime;
        this.type = type;
        this.isMe = isMe;
        this.favorite_restrooms = favorite_restrooms;
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

    public boolean isMe() {
        return isMe;
    }

    public void setMe(boolean me) {
        isMe = me;
    }

    public ArrayList<RestroomData> getFavorite_restrooms() {
        return favorite_restrooms;
    }

    public void setFavorite_restrooms(ArrayList<RestroomData> favorite_restrooms) {
        this.favorite_restrooms = favorite_restrooms;
    }
}
