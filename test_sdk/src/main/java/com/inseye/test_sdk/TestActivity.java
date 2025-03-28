package com.inseye.test_sdk;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.inseye.sdk.GazeDataExtension;
import com.inseye.sdk.GazeDataReader;
import com.inseye.sdk.InseyeTracker;
import com.inseye.sdk.InseyeSDK;
import com.inseye.sdk.InseyeTrackerException;
import com.inseye.sdk.ScreenUtils;
import com.inseye.shared.communication.GazeData;
import com.inseye.shared.communication.TrackerAvailability;

import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

public class TestActivity extends AppCompatActivity implements GazeDataReader.IGazeData, InseyeTracker.IEyeTrackerStatusListener {

    private static final String TAG = TestActivity.class.getSimpleName();
    private TextView statusTextView, gazeDataTextView, additionalInfoTextView;
    private Button calibrateButton, subGazeDataButton, unsubGazeDataButton;
    private OverlayRedPointView redPointView;

    private InseyeSDK inseyeSDK;
    private InseyeTracker inseyeTracker;
    private ScreenUtils screenUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        setContentView(R.layout.activity_test);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);


        calibrateButton = findViewById(R.id.calibButton);
        subGazeDataButton = findViewById(R.id.subscribeGazeButton);
        unsubGazeDataButton = findViewById(R.id.unsubscribeGazeButton);
        statusTextView = findViewById(R.id.textViewStatus);
        gazeDataTextView = findViewById(R.id.textViewGazeData);
        additionalInfoTextView = findViewById(R.id.additionalInfoText);
        redPointView = findViewById(R.id.redPointView);

        inseyeSDK = new InseyeSDK(this);

        inseyeSDK.getEyeTracker().thenAccept(insEyeTracker -> {
            this.inseyeTracker = insEyeTracker;
            this.screenUtils = insEyeTracker.getScreenUtils();
            statusTextView.setText(String.format("Status: %s", insEyeTracker.getTrackerAvailability().name()));

            UpdateAdditionalInfo();

            insEyeTracker.subscribeToTrackerStatus(this);
            calibrateButton.setOnClickListener(v -> {
                insEyeTracker.startCalibration().thenAccept(result -> {
                    Toast.makeText(this, result.toString(), Toast.LENGTH_SHORT).show();
                });
            });

            subGazeDataButton.setOnClickListener(v -> {
                try {
                    insEyeTracker.startStreamingGazeData();
                    insEyeTracker.subscribeToGazeData(this);
                } catch (InseyeTrackerException e) {
                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
            unsubGazeDataButton.setOnClickListener(v -> {
                insEyeTracker.stopStreamingGazeData();
                insEyeTracker.unsubscribeFromGazeData(this);});


        }).exceptionally(throwable -> {
            Toast.makeText(this, throwable.getMessage(), Toast.LENGTH_SHORT).show();
            Log.e(TAG, throwable.getMessage());
            return null;
        });
    }

    private void UpdateAdditionalInfo() {
        additionalInfoTextView.post(() -> additionalInfoTextView.setText(String.format("Other Info\n\nDominant Eye: %s\nFov: %s\nService: %s\nCalibration: %s\nFirmware: %s",
                inseyeTracker.getDominantEye(), inseyeTracker.getVisibleFov(), inseyeTracker.getServiceVersion(), inseyeTracker.getCalibrationVersion(), inseyeTracker.getFirmwareVersion())));
    }

    @Override
    protected void onDestroy() {
        inseyeSDK.dispose();
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        if(inseyeSDK.isServiceConnected()) {
            statusTextView.setText(String.format("Status: %s", inseyeTracker.getTrackerAvailability().name()));
            UpdateAdditionalInfo();
        }
        super.onResume();
    }

    @Override
    public void nextGazeDataReady(GazeData gazeData) {
        gazeDataTextView.post(() -> gazeDataTextView.setText(String.format("Gaze Data\n\nLeft Eye:   X:%6.2f Y:%6.2f\nRight Eye:   X:%6.2f Y:%6.2f\nEvent: %s\nTime: %d",
                gazeData.left_x, gazeData.left_y, gazeData.right_x, gazeData.right_y, gazeData.event, gazeData.timeMilli)));
        Vector2D gazeMidPoint = GazeDataExtension.getGazeCombined(gazeData);
        Vector2D gazeViewSpace = screenUtils.angleToAbsoluteScreenSpace(gazeMidPoint);
        redPointView.post(() -> redPointView.setPoint(gazeViewSpace));
    }

    @Override
    public void onTrackerAvailabilityChanged(TrackerAvailability availability) {
        statusTextView.post(() -> statusTextView.setText(String.format("Status: %s", availability.name())));
    }
}