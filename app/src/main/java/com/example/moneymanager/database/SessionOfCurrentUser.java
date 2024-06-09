package com.example.moneymanager.database;

public class SessionOfCurrentUser {
    private static String userMail;

    public static void saveUserMail(String mail){
        userMail = mail;
    }

    public static String getUserMail(){
        return userMail;
    }
}
