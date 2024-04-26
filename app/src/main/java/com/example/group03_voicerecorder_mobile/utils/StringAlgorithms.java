package com.example.group03_voicerecorder_mobile.utils;

public class StringAlgorithms {
    public static final int d = 256;

    public static void search(String txt, String pat, int q) {
        if (txt == null || pat == null) {
            System.out.println("txt is null or pat is null");
            return;
        }
        int n = txt.length();
        int m = pat.length();

        // Hash value for the pattern and the initial window of text
        int p = 0, t = 0;
        int h = 1;

        for (int i = 0; i < m - 1; i++) {
            h = (h * d) % q; // Calculate the hash value for the pattern
        }

        for (int i = 0; i < m; i++) {
            p = (p * d + pat.charAt(i)) % q;
            t = (t * d + txt.charAt(i)) % q;
        }

        // Slide the pattern over the text
        for (int i = 0; i <= n - m; i++) {

            // Check if the current hash values match
            if (p == t) {
                // Pattern found
                boolean found = true;
                for (int j = 0; j < m; j++) {
                    if (txt.charAt(i + j) != pat.charAt(j)) {
                        found = false;
                        break;
                    }
                }
                if (found) {
                    System.out.println("Pattern found at index " + i);
                }
            }

            // Recompute hash value for the next window of text
            if (i < n - m) {
                t = (d * (t - txt.charAt(i) * h) + txt.charAt(i + m)) % q;
                if (t < 0) {
                    t = (t + q);
                }
            }
        }
    }
}
