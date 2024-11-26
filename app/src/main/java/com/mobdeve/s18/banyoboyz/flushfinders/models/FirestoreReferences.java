package com.mobdeve.s18.banyoboyz.flushfinders.models;

public class FirestoreReferences
{
    public static class Accounts
    {
        public final static String COLLECTION = "ACCOUNT";
        public final static String NAME = "name";
        public final static String PASSWORD = "password";
        public final static String IS_ACTIVE = "isActive";
        public final static String PROFILE_PICTURE = "profilePicture";
        public final static String CREATION_TIME = "creationTime";
        public final static String TYPE = "type";
        public final static String FAVORITE_RESTROOMS = "favoriteRestrooms";
    }

    public static class Buildings
    {
        public final static String COLLECTION = "BUILDING";
        public final static String LATITUDE = "latitude";
        public final static String LONGITUDE = "longitude";
        public final static String NAME = "name";
        public final static String ADDRESS = "address";
        public final static String BUILDING_PICTURE = "buildingPicture";
        public final static String RESTROOMS = "restrooms";
        public final static String SUGGESTION = "suggestion";
    }

    public static class Restrooms
    {
        public final static String COLLECTION = "RESTROOM";
        public final static String NAME = "name";
        public final static String CLEANLINESS = "cleanliness";
        public final static String MAINTENANCE = "maintenance";
        public final static String VACANCY = "vacancy";
        public final static String AMENITIES = "amenities";
    }

    public static class Amenities
    {
        public final static String COLLECTION = "AMENITY";
        public final static String PICTURE = "picture";
    }

    public static class Reviews
    {
        public final static String COLLECTION = "REVIEW";
        public final static String RESTROOM = "restroom";
        public final static String REVIEWER = "reviewer";
        public final static String RATING = "rating";
        public final static String REPORT = "report";
        public final static String CLEANLINESS = "cleanliness";
        public final static String MAINTENANCE = "maintenance";
        public final static String VACANCY = "vacancy";
    }
}
