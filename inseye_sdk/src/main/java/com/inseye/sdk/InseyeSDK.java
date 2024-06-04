package com.inseye.sdk;

import android.content.Context;

import com.inseye.shared.communication.ISharedService;

import java.util.concurrent.CompletableFuture;

public class InseyeSDK {
    private final Context context;
    public InseyeSDK(Context context) {
        this.context = context;
    }


    public boolean isServiceConnected() {
        return true;
    }



}
