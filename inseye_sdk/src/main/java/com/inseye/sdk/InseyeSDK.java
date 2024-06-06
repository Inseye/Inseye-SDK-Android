package com.inseye.sdk;

import android.content.Context;
import android.os.RemoteException;
import android.util.Log;

import com.inseye.shared.communication.ISharedService;

import java.util.concurrent.CompletableFuture;

public class InseyeSDK {
    private static final String TAG = InseyeSDK.class.getSimpleName();

    private final Context context;
    private final InseyeServiceBinder serviceBinder;
    public InseyeSDK(Context context) {
        this.context = context;
        serviceBinder = new InseyeServiceBinder(context);
    }


    public boolean isServiceConnected() {
        return serviceBinder.isConnected();
    }

    public CompletableFuture<InsEyeTracker> getEyeTracker() {
        CompletableFuture<InsEyeTracker> trackerFuture = new CompletableFuture<>();
        serviceBinder.bind(new InseyeServiceBinder.IServiceBindCallback() {
            @Override
            public void serviceConnected(ISharedService service) {
                try {
                    Log.i(TAG, "service connected " + service.getTrackerAvailability().toString());
                } catch (RemoteException e) {
                    throw new RuntimeException(e);
                }
                trackerFuture.complete(new InsEyeTracker(service));
            }

            @Override
            public void serviceDisconnected() {
                Log.i(TAG, "service disconnected");

                trackerFuture.completeExceptionally(new Exception("service disconnected"));
            }

            @Override
            public void serviceError(Exception e) {
                Log.e(TAG, "service connection error: " + e.getMessage());
                trackerFuture.completeExceptionally(e);
            }


        });
        return trackerFuture;
    }



}
