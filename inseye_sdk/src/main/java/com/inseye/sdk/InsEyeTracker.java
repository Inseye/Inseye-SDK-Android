package com.inseye.sdk;

import android.os.RemoteException;
import android.util.Log;

import com.inseye.shared.communication.ActionResult;
import com.inseye.shared.communication.GazeData;
import com.inseye.shared.communication.IBuiltInCalibrationCallback;
import com.inseye.shared.communication.IServiceBuiltInCalibrationCallback;
import com.inseye.shared.communication.ISharedService;
import com.inseye.shared.communication.Version;
import com.inseye.shared.communication.TrackerAvailability;

import java.util.concurrent.CompletableFuture;

public class InsEyeTracker {
    private static final String TAG = InsEyeTracker.class.getSimpleName();

    private final ISharedService serviceInterface;

    private Version serviceVersion;
    private Version firmwareVersion;

    private IServiceBuiltInCalibrationCallback calibrationAbortHandler;

    protected InsEyeTracker(ISharedService serviceInterface) {
        this.serviceInterface = serviceInterface;
        try {
            serviceInterface.getVersions(serviceVersion, firmwareVersion);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }

    }

    public Version getServiceVersion() {
        return serviceVersion;
    }

    public Version getFirmwareVersion(){
        return firmwareVersion;
    }


    public TrackerAvailability getTrackerAvailability() {
        try {
            return serviceInterface.getTrackerAvailability();
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }


    public GazeData getLastGazeData() {
        return null;
    }

    public void subscribeToGazeData(GazeDataReader.IGazeData gazeData){

    }

    public void unsubscribeToGazeData() {

    }

    public void abortCalibration() {
        try {
            if(calibrationAbortHandler != null) calibrationAbortHandler.abortCalibrationProcedure();
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }


    public CompletableFuture<ActionResult> startCalibration() {
        CompletableFuture<ActionResult> calibrationFuture = new CompletableFuture<>();
        ActionResult result = new ActionResult();
        try {
            calibrationAbortHandler = serviceInterface.startBuiltInCalibrationProcedure(result, new IBuiltInCalibrationCallback.Stub() {
                @Override
                public void finishCalibration(ActionResult calibrationResult) throws RemoteException {
                    Log.i(TAG, calibrationResult.toString());
                    if(calibrationResult.successful) {
                        Log.e(TAG, "calibration success");
                        calibrationFuture.complete(ActionResult.success());
                        //Toast.makeText(con, "calibration success", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        calibrationFuture.complete(ActionResult.error(calibrationResult.errorMessage));

                        Log.e(TAG, "calibration fail: " + calibrationResult.errorMessage);
                        //Toast.makeText(MainActivity.this, "calibration fail: " + calibrationResult.errorMessage, Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } catch (RemoteException e) {
            Log.e(TAG, "calibration remote exception: " + e);
            calibrationFuture.completeExceptionally(e);
        }
        if(result.successful) {
            Log.i(TAG, "calibration init success");
            //Toast.makeText(MainActivity.this, "calibration init success", Toast.LENGTH_SHORT).show();
            //calibrationAbortHandler could be used to abort calibration
        }
        else {
            Log.e(TAG, "calibration init fail: " + result.errorMessage);
            calibrationFuture.complete(ActionResult.error(result.errorMessage));

            //Toast.makeText(this, "calibration init fail: " + result.errorMessage, Toast.LENGTH_SHORT).show();
            //calibrationAbortHandler is null
        }
        return calibrationFuture;
    }






}
