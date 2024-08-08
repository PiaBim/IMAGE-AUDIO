package com.example.a0808;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.TaskStackBuilder;

public class StopWatch_Main extends AppCompatActivity {

    private TextView textViewTime;
    private Button buttonStart, buttonStop, buttonReset;

    private Handler handler;
    private long startTime, elapsedTime;
    private boolean running;

    private Runnable updateTimerThread = new Runnable() {
        @Override
        public void run() {
            if (running) {
                elapsedTime = System.currentTimeMillis() - startTime;
                int seconds = (int) (elapsedTime / 1000);
                int minutes = seconds / 60;
                int hours = minutes / 60;
                seconds = seconds % 60;
                minutes = minutes % 60;

                String time = String.format("%02d:%02d:%02d", hours, minutes, seconds);
                textViewTime.setText(time);
                handler.postDelayed(this, 1000);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.stopwatch);

        textViewTime = findViewById(R.id.textViewTime);
        buttonStart = findViewById(R.id.buttonStart);
        buttonStop = findViewById(R.id.buttonStop);
        buttonReset = findViewById(R.id.buttonReset);

        handler = new Handler(Looper.getMainLooper());

        buttonStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startTimer();
            }
        });

        buttonStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopTimer();
            }
        });

        buttonReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetTimer();
            }
        });
    }

    private void startTimer() {
        if (!running) {
            startTime = System.currentTimeMillis() - elapsedTime;
            handler.postDelayed(updateTimerThread, 0);
            running = true;
        }
    }

    private void stopTimer() {
        if (running) {
            handler.removeCallbacks(updateTimerThread);
            running = false;
        }
    }

    private void resetTimer() {
        stopTimer();
        elapsedTime = 0;
        textViewTime.setText("00:00:00");
    }

    @Override
    public void onCreateSupportNavigateUpTaskStack(@NonNull TaskStackBuilder builder) {
        super.onCreateSupportNavigateUpTaskStack(builder);
    }
}
