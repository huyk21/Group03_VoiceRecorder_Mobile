package com.example.group03_voicerecorder_mobile.app.main;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.example.group03_voicerecorder_mobile.R;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

public class WaveformView extends View {
    private Queue<Float> amplitudes;
    private float width = 5f; // width of waveform lines
    private float space = 1f; // space between waveform lines
    private int maxAmplitudesToDisplay;
    private Paint linePaint; // Paint for the waveform lines
    private Paint centerLinePaint; // Paint for the central line
    private Paint backgroundPaint; // Paint for the background
    public WaveformView(Context context, AttributeSet attrs) {
        super(context, attrs);
        amplitudes = new LinkedList<>();
        linePaint = new Paint();
        linePaint.setColor(Color.WHITE); // white color for waveform lines
        linePaint.setStrokeWidth(width);

        centerLinePaint = new Paint();
        centerLinePaint.setColor(Color.RED); // red color for the central line
        centerLinePaint.setStrokeWidth(6f); // width for the central line


        backgroundPaint = new Paint();
        backgroundPaint.setColor(Color.parseColor("#80F5F5F5")); // Adjust the color and transparency as needed
    }

    public void addAmplitude(float amplitude) {
        if (maxAmplitudesToDisplay == -1) {
            calculateMaxAmplitudesToDisplay();
        }
        if (amplitudes.size() == maxAmplitudesToDisplay) {
            amplitudes.poll(); // remove the oldest amplitude
        }
        amplitudes.offer(amplitude); // add the latest amplitude
        invalidate(); // will trigger onDraw
    }

    private void calculateMaxAmplitudesToDisplay() {
        float viewWidth = getWidth() - getPaddingLeft() - getPaddingRight();
        maxAmplitudesToDisplay = (int) (viewWidth / (width + space));
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        calculateMaxAmplitudesToDisplay();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawRect(0, 0, getWidth(), getHeight(), backgroundPaint);
        super.onDraw(canvas);
        int centerY = getHeight() / 2;
        int centerX = getWidth() / 2;

        // Set the length of the central line (e.g., half of the view's height)
        float centralLineLength = getHeight() ;
        float centralLineTop = centerY - (centralLineLength / 2);
        float centralLineBottom = centerY + (centralLineLength / 2);

        // Draw the central red line with the new top and bottom points
        canvas.drawLine(centerX, centralLineTop, centerX, centralLineBottom, centerLinePaint);

        // Start drawing the waveform to the left of the central line
        int xPosToLeft = centerX;

        // Drawing the waveform to the right side as default (flat line)
        canvas.drawLine(centerX, centerY, getWidth(), centerY, linePaint);

        // Iterate through the amplitudes backwards to draw from center to left
        for (int i = amplitudes.size() - 1; i >= 0; i--) {
            float amplitude = (float) ((LinkedList) amplitudes).get(i);
            // Scaled amplitude relative to the height of the view, 32768 is the max amplitude for 16-bit PCM
            float scaledAmplitude = (amplitude / 32768f) * centerY * 2f;

            // Draw the waveform line for this amplitude segment
            xPosToLeft -= (width + space);
            canvas.drawLine(xPosToLeft, centerY - scaledAmplitude, xPosToLeft, centerY + scaledAmplitude, linePaint);

            // If we reached the beginning of the view, stop drawing
            if (xPosToLeft < 0) {
                break;
            }
        }
    }
    // Method to update the displayed waveform based on playback position
    public void setPlaybackPosition(ArrayList<Integer> amplitudeList, int playbackPositionMs, int fileDurationMs) {
        // Calculate the index in the amplitude array that corresponds to the current playback position
        int totalAmplitudeCount = amplitudeList.size();
        float relativePosition = ((float) playbackPositionMs) / fileDurationMs;
        int startIndex = Math.round(relativePosition * totalAmplitudeCount);

        // Clear the current amplitudes in the view
        amplitudes.clear();

        // Load amplitudes into the waveform view from the calculated start index
        for (int i = startIndex; i < startIndex + maxAmplitudesToDisplay && i < totalAmplitudeCount; i++) {
            float amplitude = amplitudeList.get(i);
            amplitudes.offer(amplitude);
        }

        invalidate(); // Redraw the waveform with the new data
    }
    public void resetWaveform() {
        amplitudes.clear(); // Clear the amplitudes queue
        invalidate(); // Redraw the waveform
    }
}
