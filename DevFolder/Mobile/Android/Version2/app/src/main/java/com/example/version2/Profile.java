package com.example.version2;

public class Profile {

    private static String name;
    private static String category;
    private static String email;
    private static String type;

    public Profile() {

    }

    public static void setName(String fname) {
        Profile.name = fname;
    }

    public static void setCategory(String category) {
        Profile.category = category;
    }

    public static void setEmail(String email) {
        Profile.email = email;

    }

    public static void setType(String type) {
        Profile.type = type;

    }

    public static String getName() {
        return name;
    }

    public static String getCategory() {
        return category;
    }

    public static String getEmail() {
        return email;
    }

    public static String getType() {
        return type;
    }
}
