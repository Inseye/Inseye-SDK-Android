package com.inseye.sdk;

import android.content.Context;

import com.inseye.shared.communication.ISharedService;

import java.util.concurrent.CompletableFuture;

public class InseyeSDK {
    private final Context context;
    private InseyeServiceBinder serviceBinder;
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
                trackerFuture.complete(new InsEyeTracker(service));
            }

            @Override
            public void serviceDisconnected() {
                trackerFuture.completeExceptionally(new Exception("service disconnected"));
            }

            @Override
            public void serviceError(Exception e) {
                trackerFuture.completeExceptionally(e);
            }


        });
        return trackerFuture;
    }



}
