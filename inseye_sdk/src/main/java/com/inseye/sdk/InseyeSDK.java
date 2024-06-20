package com.inseye.sdk;

import android.content.Context;
import android.util.Log;

import com.inseye.shared.communication.ISharedService;

import java.util.concurrent.CompletableFuture;

import lombok.Getter;

/**
 * The main entry point for interacting with the Inseye SDK.
 */
public class InseyeSDK {
    private static final String TAG = InseyeSDK.class.getSimpleName();

    private final InseyeServiceBinder serviceBinder;
    /**
     * The InseyeTracker instance.
     */
    @Getter
    private InseyeTracker inseyeTracker = null;

    /**
     * Constructs a new InseyeSDK instance.
     *
     * @param context The application context.
     */
    public InseyeSDK(Context context) {
        serviceBinder = new InseyeServiceBinder(context);
    }

    /**
     * Checks if the Inseye service is connected.
     *
     * @return True if the service is connected, false otherwise.
     */
    public boolean isServiceConnected() {
        return serviceBinder.isConnected();
    }

    /**
     * Asynchronously retrieves an instance of {@link InseyeTracker}.
     *
     * @return A CompletableFuture that completes with an InseyeTracker instance when the service is connected,
     *         or completes exceptionally if an error occurs.
     */
    public CompletableFuture<InseyeTracker> getEyeTracker() {
        CompletableFuture<InseyeTracker> trackerFuture = new CompletableFuture<>();
        serviceBinder.bind(new InseyeServiceBinder.IServiceBindCallback() {
            @Override
            public void serviceConnected(ISharedService service) {
                inseyeTracker = new InseyeTracker(service);
                trackerFuture.complete(inseyeTracker);
            }

            @Override
            public void serviceDisconnected() {
                Log.i(TAG, "service disconnected");
                trackerFuture.completeExceptionally(new InseyeTrackerException("service disconnected"));
            }

            @Override
            public void serviceError(Exception e) {
                Log.e(TAG, "service connection error: " + e.getMessage());
                trackerFuture.completeExceptionally(new InseyeTrackerException(e));
            }
        });

        return trackerFuture;
    }

    /**
     Disposes InseyeTracker instance.
     */
    public void dispose() {
        if(inseyeTracker != null) {
            inseyeTracker.unsubscribeFromTrackerStatus();
            inseyeTracker.unsubscribeFromGazeData();
        }
        serviceBinder.unbind();


    }
}