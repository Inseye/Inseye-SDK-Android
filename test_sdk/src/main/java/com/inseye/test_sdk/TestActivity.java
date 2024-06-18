package com.inseye.test_sdk;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.inseye.sdk.GazeDataReader;
import com.inseye.sdk.InseyeTracker;
import com.inseye.sdk.InseyeSDK;
import com.inseye.sdk.InseyeTrackerException;
import com.inseye.sdk.RedPointView;
import com.inseye.shared.communication.GazeData;
import com.inseye.shared.communication.TrackerAvailability;

import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;
import java.util.function.Function;

public class TestActivity extends AppCompatActivity implements GazeDataReader.IGazeData, InseyeTracker.IEyeTrackerStatusListener {

    private static final String TAG = TestActivity.class.getSimpleName();
    private TextView statusTextView, gazeDataTextView;
    private Button calibrateButton, subGazeDataButton, unsubGazeDataButton;
    private RedPointView redPointView;

    private final Handler mainLooperHandler = new Handler(Looper.getMainLooper());

    private InseyeSDK inseyeSDK;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION|
                        View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);


        setContentView(R.layout.activity_test);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        calibrateButton = findViewById(R.id.calibButton);
        subGazeDataButton = findViewById(R.id.subscribeGazeButton);
        unsubGazeDataButton = findViewById(R.id.unsubscribeGazeButton);
        statusTextView = findViewById(R.id.textViewStatus);
        gazeDataTextView = findViewById(R.id.textViewGazeData);
        redPointView = findViewById(R.id.redPointView);

        inseyeSDK = new InseyeSDK(this);

//        InseyeTracker eyeTracker = null;
//        try {
//            eyeTracker = inseyeSDK.getEyeTracker().get();
//        } catch (ExecutionException | InterruptedException e) {
//            Log.e(TAG, e.getMessage());
//            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
//        }

//        if(eyeTracker != null && inseyeSDK.isServiceConnected()) {

            inseyeSDK.getEyeTracker().thenAccept(insEyeTracker -> {
                statusTextView.setText(insEyeTracker.getTrackerAvailability().name());

                insEyeTracker.subscribeToTrackerStatus(this);

                calibrateButton.setOnClickListener(v -> {
                    insEyeTracker.startCalibration().thenAccept(result -> {
                        Toast.makeText(this, result.toString(), Toast.LENGTH_SHORT).show();
                    });
                });

                subGazeDataButton.setOnClickListener(v -> {
                    try {
                        insEyeTracker.subscribeToGazeData(this);
                    } catch (InseyeTrackerException e) {
                        Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

                unsubGazeDataButton.setOnClickListener(v -> insEyeTracker.unsubscribeFromGazeData());
                Toast.makeText(this, insEyeTracker.getVisibleFov().toString(), Toast.LENGTH_SHORT).show();

                Toast.makeText(this, insEyeTracker.getCalibrationVersion().toString(), Toast.LENGTH_SHORT).show();


            }).exceptionally(throwable -> {
                Toast.makeText(this, throwable.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e(TAG, throwable.getMessage());
                return null;
            });
    }

    @Override
    protected void onDestroy() {
        inseyeSDK.disposeEyeTracker();
        super.onDestroy();
    }

    @Override
    public void nextGazeDataReady(GazeData gazeData) {
        mainLooperHandler.post(() -> gazeDataTextView.setText(String.format("Left Eye:   X:%6.2f Y:%6.2f\nRight Eye: X:%6.2f Y:%6.2f\nEvent: %s\nTime: %d",
                gazeData.left_x, gazeData.left_y, gazeData.right_x, gazeData.right_y, gazeData.event, gazeData.timeMilli)));
        float avgGazeX = (gazeData.left_x + gazeData.right_x) / 2f;
        float avgGazeY = (gazeData.left_y + gazeData.right_y) / 2f;
        // gaze data in radians where (0,0) is in screen center
        redPointView.post(()-> redPointView.setPoint(avgGazeX, avgGazeY));
    }

    @Override
    public void onTrackerAvailabilityChanged(TrackerAvailability availability) {
        mainLooperHandler.post(() -> statusTextView.setText(availability.name()));
    }
}