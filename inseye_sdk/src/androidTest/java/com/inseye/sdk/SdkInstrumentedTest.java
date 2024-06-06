package com.inseye.sdk;

import android.content.Context;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.ExecutionException;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class SdkInstrumentedTest {
    @Test
    public void getEyeTrackerTestSync() {
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();

        InseyeSDK inseyeSDK = new InseyeSDK(appContext);

        try {
            InsEyeTracker eyeTracker = inseyeSDK.getEyeTracker().get();
            System.out.println("tracker status: " + eyeTracker.getTrackerAvailability());
            System.out.println("service: " + eyeTracker.getServiceVersion());
            System.out.println("firmware: " + eyeTracker.getFirmwareVersion());
        } catch (ExecutionException | InterruptedException e) {
            System.out.println(e);
        }
    }

    @Test
    public void getEyeTrackerTestAsync() {
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();

        InseyeSDK inseyeSDK = new InseyeSDK(appContext);

        inseyeSDK.getEyeTracker().thenAccept(insEyeTracker -> {
            System.out.println("tracker status: " + insEyeTracker.getTrackerAvailability());
            System.out.println("service version: " + insEyeTracker.getServiceVersion());
            System.out.println("firmware: " + insEyeTracker.getFirmwareVersion());

        }).exceptionally(throwable -> {
            System.out.println(throwable.toString());
            return null;
        });
    }
}