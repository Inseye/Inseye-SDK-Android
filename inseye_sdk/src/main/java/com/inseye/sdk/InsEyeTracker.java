package com.inseye.sdk;

import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.inseye.shared.communication.ActionResult;
import com.inseye.shared.communication.GazeData;
import com.inseye.shared.communication.IBuiltInCalibrationCallback;
import com.inseye.shared.communication.IServiceBuiltInCalibrationCallback;
import com.inseye.shared.communication.ISharedService;
import com.inseye.shared.communication.IntActionResult;
import com.inseye.shared.communication.Version;
import com.inseye.shared.communication.TrackerAvailability;

import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;

public class InsEyeTracker {
    private static final String TAG = InsEyeTracker.class.getSimpleName();

    private final ISharedService serviceInterface;

    private Version serviceVersion;
    private Version firmwareVersion;

    private IServiceBuiltInCalibrationCallback calibrationAbortHandler;
    private GazeDataReader gazeDataReader;


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


    public GazeData getMostRecentGazeData() {
        return null;
    }

    public void subscribeToGazeData(@NonNull GazeDataReader.IGazeData gazeData){
        try {
            //int port = serviceInterface.isStreamingGazeData();
            IntActionResult result = serviceInterface.startStreamingGazeData();
            if(result.success) {
                int udpPort = result.value;
                Log.i(TAG, "port:" + udpPort);
                gazeDataReader = new GazeDataReader(udpPort, gazeData);
                gazeDataReader.start();
            } else {
                Log.e(TAG, "gaze stream error: " + result.errorMessage);
                //Toast.makeText(this, "gaze stream error: " + result.errorMessage, Toast.LENGTH_SHORT).show();
            }

        } catch (RemoteException | SocketException | UnknownHostException e) {
            Log.e(TAG, e.toString());
            //Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }



    }

    public void unsubscribeToGazeData() {
        try {
            serviceInterface.stopStreamingGazeData();
            gazeDataReader.interrupt();
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
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
