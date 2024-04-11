package com.example.group03_voicerecorder_mobile.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Utilities {

    // Method to convert a string date to a Date object
    public static Date stringToDate(String dateString, String pattern) {
        System.out.println(dateString + " " + pattern);
        SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
        try {
            return dateFormat.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
            return null; // Return null if parsing fails
        }
    }

    // Method to convert a Date object to a string date
    public static String dateToString(Date date, String pattern) {
        if (date != null) {
            SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
            return dateFormat.format(date);
        } else {
            return "N/A"; // Or any other default value you want to use when timestamp is null
        }
    }
}
