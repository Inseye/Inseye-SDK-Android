package com.inseye.test_sdk;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.inseye.sdk.InsEyeTracker;
import com.inseye.sdk.InseyeSDK;

import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;
import java.util.function.Function;

public class TestActivity extends AppCompatActivity {

    private static final String TAG = TestActivity.class.getSimpleName();
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

        InseyeSDK inseyeSDK = new InseyeSDK(this);

        InsEyeTracker eyeTracker;
        try {
            eyeTracker = inseyeSDK.getEyeTracker().get();
        } catch (ExecutionException | InterruptedException e) {
            Log.e(TAG, e.getMessage());
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }

//        inseyeSDK.getEyeTracker().thenAccept(insEyeTracker -> {
//
//        }).exceptionally(throwable ->{
//            Toast.makeText(this, throwable.getMessage(), Toast.LENGTH_SHORT).show();
//            return null;
//        });
    }
}