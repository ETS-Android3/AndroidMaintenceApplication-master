package com.tinu.maintanceappliccation.restApiCall;

/**
 * Created by admin on 5/17/2017.
 */

public class GloablMethods {
    public static final String API_HEADER = "Bearer ";
    public static final String HEADER_AUTHORIZATION = "Authorization";
    public static boolean validate(String... strings) {
        for (String string : strings) {
            if (string == null) return false;
            if (string.length() < 1) return false;
        }
        return true;
    }
}
