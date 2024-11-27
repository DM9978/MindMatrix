package com.dini.mindmatrix.engine;

import java.util.HashMap;
import java.util.Map;

public class Session {
    private static boolean isUserLoggedIn = false;
    private static int loggedInUserId = -1;
    private static String loggedInUsername = null;
    private static Map<String, String> cookies = new HashMap<>();

    public static boolean isUserLoggedIn() {
        return isUserLoggedIn;
    }

    public static void setUserLoggedIn(boolean isLoggedIn) {
        isUserLoggedIn = isLoggedIn;
    }

    public static int getLoggedInUserId() {
        return loggedInUserId;
    }

    public static void setLoggedInUserId(int userId) {
        loggedInUserId = userId;
    }

    public static String getLoggedInUsername() {
        return loggedInUsername;
    }

    public static void setLoggedInUsername(String username) {
        loggedInUsername = username;
    }

    public static void addCookie(String key, String value) {
        cookies.put(key, value);
    }

    public static String getCookie(String key) {
        return cookies.get(key);
    }

    public static void removeCookie(String key) {
        cookies.remove(key);
    }

    public static void clearCookies() {
        cookies.clear();
    }

    public static void resetSession() {
        isUserLoggedIn = false;
        loggedInUserId = -1;
        loggedInUsername = null;
        clearCookies();
    }
}
