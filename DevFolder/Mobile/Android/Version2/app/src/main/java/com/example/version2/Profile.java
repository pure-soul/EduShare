package com.example.version2;

public class Profile {

    private static String name;
//    private static String lname;
    private static String email;
    private static String type;

    public Profile() {

    }

    public static void setName(String fname) {
        Profile.name = fname;
    }

//    public static void setLName(String lname) {
//        Profile.lname = lname;
 //   }

    public static void setEmail(String email) {
        Profile.email = email;

    }

    public static void setType(String type) {
        Profile.type = type;

    }

    public static String getName() {
        return name;
    }

//    public static String getLName() {
//        return lname;
 //   }

    public static String getEmail() {
        return email;
    }

    public static String getType() {
        return type;
    }
}
