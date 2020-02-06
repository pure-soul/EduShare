package com.example.original;

public class Profile {

    private static String name;
    private static String email;

    public Profile() {

    }

    public static void setName(String name) {
        Profile.name = name;
    }

    public static void setEmail(String email) {
        Profile.email = email;

    }

    public static String getName() {
        return name;
    }

    public static String getEmail() {
        return email;
    }
}
