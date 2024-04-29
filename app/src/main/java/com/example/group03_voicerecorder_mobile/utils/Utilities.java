package com.example.group03_voicerecorder_mobile.utils;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.example.group03_voicerecorder_mobile.R;
import com.example.group03_voicerecorder_mobile.app.GlobalConstants;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
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
        System.out.println("changeFileName" + filePath);
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

    public static int deleteFile(String filePath, Context context) {
        File fileToDelete = new File(filePath);

        if (fileToDelete.exists()) {
            boolean deleted = fileToDelete.delete();
            if (deleted) {
                // File deleted successfully
//                Toast.makeText(context, "File deleted", Toast.LENGTH_SHORT).show();
                return 1;
            } else {
                // Deletion failed
//                Toast.makeText(context, "Failed to delete file", Toast.LENGTH_SHORT).show();
                return -1;
            }
        } else {
            // File doesn't exist
//            Toast.makeText(context, "File not found", Toast.LENGTH_SHORT).show();
            return -2;
        }
    }

    public static void overwriteAudioFile(Context context, String fileName, InputStream inputStream) throws IOException {
        File file = new File(context.getExternalFilesDir(null).getAbsolutePath() + "/" + fileName);

        Log.d("overWriteFile", "Overwrite " + fileName);
        FileOutputStream outputStream = new FileOutputStream(file, false);

        // Use a buffer for efficient copying
        byte[] buffer = new byte[1024]; // Adjust buffer size as needed
        int bytesRead;
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, bytesRead);
        }

        inputStream.close();
        outputStream.close();
    }

    public static void setCustomTheme(Context context) {
        String selectedTheme = PreferenceHelper.getSelectedTheme(context, "selectedTheme");
        switch (selectedTheme) {
            case GlobalConstants.THEME_BLUE:
            {
                context.setTheme(R.style.AppTheme_Blue);
                break;
            }
            case GlobalConstants.THEME_TEAL:
            {
                context.setTheme(R.style.AppTheme_Teal);
                break;
            }
            case GlobalConstants.THEME_RED:
            {
                context.setTheme(R.style.AppTheme_Red);
                break;
            }
            case GlobalConstants.THEME_PINK:
            {
                context.setTheme(R.style.AppTheme_Pink);
                break;
            }
            case GlobalConstants.THEME_PURPLE:
            {
                context.setTheme(R.style.AppTheme_Purple);
                break;
            }
            case GlobalConstants.THEME_ORANGE:
            {
                context.setTheme(R.style.AppTheme_DeepOrange);
                break;
            }
            default: {
                context.setTheme(R.style.AppTheme_Default);
                break;
            }
        }
    }

}
