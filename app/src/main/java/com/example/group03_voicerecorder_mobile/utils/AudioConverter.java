package com.example.group03_voicerecorder_mobile.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class AudioConverter {

    public static void convert(String inputFilePath, String outputFilePath) throws IOException, InterruptedException {
        // Replace these paths with the location of your FFmpeg binary
        String ffmpegPath = "/path/to/ffmpeg";  // Example: "/usr/bin/ffmpeg"

        // Command to convert audio file using FFmpeg
        String[] command = {
                ffmpegPath,
                "-i", inputFilePath,  // Input file path
                "-acodec", "copy",   // Copy audio codec
                outputFilePath       // Output file path
        };

        // Execute command
        ProcessBuilder processBuilder = new ProcessBuilder(command);
        Process process = processBuilder.start();

        // Read output messages from FFmpeg
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
        String line;
        while ((line = reader.readLine()) != null) {
            System.out.println(line);
        }

        // Wait for the process to finish
        int exitCode = process.waitFor();
        if (exitCode != 0) {
            throw new RuntimeException("Failed to convert audio file. FFmpeg process exited with error code: " + exitCode);
        }
    }
}

