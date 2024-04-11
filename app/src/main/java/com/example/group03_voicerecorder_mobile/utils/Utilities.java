package com.example.group03_voicerecorder_mobile.utils;

import android.content.Context;
import android.widget.Toast;

import java.io.File;
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

    public static void changeFileName(String newName, String filePath, Context context) {
        File oldFile = new File(filePath);
        String dir = oldFile.getParent();

        String newPath = dir + File.separator + newName;

        File newFile = new File(newPath);
        if (oldFile.exists()) {
            boolean renamed = oldFile.renameTo(newFile);
            if (renamed) {
                // File renamed successfully
                Toast.makeText(context, "File renamed to " + newFile.getName(), Toast.LENGTH_SHORT).show();
            } else {
                // Renaming failed
                Toast.makeText(context, "Failed to rename file", Toast.LENGTH_SHORT).show();
            }
        } else {
            // Source file doesn't exist
            Toast.makeText(context, "File not found", Toast.LENGTH_SHORT).show();
        }
    }

    public static void deleteFile(String filePath, Context context) {
        File fileToDelete = new File(filePath);

        if (fileToDelete.exists()) {
            boolean deleted = fileToDelete.delete();
            if (deleted) {
                // File deleted successfully
                Toast.makeText(context, "File deleted", Toast.LENGTH_SHORT).show();
            } else {
                // Deletion failed
                Toast.makeText(context, "Failed to delete file", Toast.LENGTH_SHORT).show();
            }
        } else {
            // File doesn't exist
            Toast.makeText(context, "File not found", Toast.LENGTH_SHORT).show();
        }

    }
}
