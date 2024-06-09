package com.inseye.sdk;

import android.os.RemoteException;
import android.util.Log;

import androidx.annotation.NonNull;

import com.inseye.shared.communication.ActionResult;
import com.inseye.shared.communication.Eye;
import com.inseye.shared.communication.GazeData;
import com.inseye.shared.communication.IBuiltInCalibrationCallback;
import com.inseye.shared.communication.IEyetrackerEventListener;
import com.inseye.shared.communication.IServiceBuiltInCalibrationCallback;
import com.inseye.shared.communication.ISharedService;
import com.inseye.shared.communication.IntActionResult;
import com.inseye.shared.communication.Version;
import com.inseye.shared.communication.TrackerAvailability;

import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.concurrent.CompletableFuture;

/**
 * The main class for interacting with the Inseye eye tracker.
 */
public class InseyeTracker {
    private static final String TAG = InseyeTracker.class.getSimpleName();
    private final ISharedService serviceInterface;
    private final Version serviceVersion = new Version();
    private final Version firmwareVersion = new Version();
    private IServiceBuiltInCalibrationCallback calibrationAbortHandler;
    private GazeDataReader gazeDataReader;

    public interface IEyeTrackerStatusListener {
        void onTrackerAvailabilityChanged(TrackerAvailability availability);
    }


    /**
     * Constructs a new InseyeTracker instance.
     *
     * @param serviceInterface The interface for communicating with the Inseye service.
     */
    protected InseyeTracker(ISharedService serviceInterface) {
        this.serviceInterface = serviceInterface;
        try {
            serviceInterface.getVersions(serviceVersion, firmwareVersion);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Returns the version of the Inseye service.
     *
     * @return The service version.
     */
    public Version getServiceVersion() {
        return serviceVersion;
    }

    /**
     * Returns the version of the eye tracker firmware.
     *
     * @return The firmware version.
     */
    public Version getFirmwareVersion(){
        return firmwareVersion;
    }

    /**
     * Returns the current availability status of the eye tracker.
     * Tracker is fully operational on status: Available
     *
     * @return The tracker availability.
     */
    public TrackerAvailability getTrackerAvailability() {
        try {
            return serviceInterface.getTrackerAvailability();
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Subscribes to eye tracker status events.
     *
     * @param statusListener The listener to receive eye tracker status events.
     */
    public void subscribeToTrackerStatus(IEyeTrackerStatusListener statusListener) {
        try {
            serviceInterface.subscribeToEyetrackerEvents(new IEyetrackerEventListener.Stub() {
                @Override
                public void handleTrackerAvailabilityChanged(TrackerAvailability availability) throws RemoteException {
                    statusListener.onTrackerAvailabilityChanged(availability);
                }
            });
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Unsubscribes from eye tracker status events.
     */
    public void unsubscribeFromTrackerStatus() {
        try {
            serviceInterface.unsubscribeFromEyetrackerEvents();
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Returns the dominant eye of the user.
     *
     * @return The dominant eye.
     */
    public Eye getDominantEye() {
        try {
            return serviceInterface.getDominantEye();
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Returns the most recent gaze data.
     *
     * @return The most recent gaze data, or null if no gaze data is available.
     */
    public GazeData getMostRecentGazeData() {
        return null; // TODO: Implement this method to return the most recent gaze data.
    }


    /**
     * Subscribes to gaze data updates.
     *
     * @param gazeData The listener to receive gaze data updates.
     * @throws InseyeTrackerException If an error occurs while subscribing to gaze data.
     */
    public void subscribeToGazeData(@NonNull GazeDataReader.IGazeData gazeData) throws InseyeTrackerException {
        try {
            IntActionResult result = serviceInterface.startStreamingGazeData();
            if(result.success) {
                int udpPort = result.value;
                Log.i(TAG, "port:" + udpPort);
                gazeDataReader = new GazeDataReader(udpPort, gazeData);
                gazeDataReader.start();
            } else {
                Log.e(TAG, "gaze stream error: " + result.errorMessage);
                throw new InseyeTrackerException(result.errorMessage);
            }

        } catch (RemoteException | SocketException | UnknownHostException e) {
            Log.e(TAG, e.toString());
            throw new InseyeTrackerException(e);
        }
    }

    /**
     * Unsubscribes from gaze data updates.
     */
    public void unsubscribeFromGazeData() {
        try {
            serviceInterface.stopStreamingGazeData();
            gazeDataReader.interrupt();
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Aborts the ongoing calibration procedure.
     */
    public void abortCalibration() {
        try {
            if(calibrationAbortHandler != null) calibrationAbortHandler.abortCalibrationProcedure();
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Starts the built-in calibration procedure.
     *
     * @return A CompletableFuture that completes when the calibration procedure finishes.
     *         The result of the future indicates whether the calibration was successful.
     */
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
                    }
                    else {
                        calibrationFuture.complete(ActionResult.error(calibrationResult.errorMessage));
                        Log.e(TAG, "calibration fail: " + calibrationResult.errorMessage);
                    }
                }
            });
        } catch (RemoteException e) {
            Log.e(TAG, "calibration remote exception: " + e);
            calibrationFuture.completeExceptionally(e);
        }
        if(result.successful) {
            Log.i(TAG, "calibration init success");
        }
        else {
            Log.e(TAG, "calibration init fail: " + result.errorMessage);
            calibrationFuture.complete(ActionResult.error(result.errorMessage));
        }
        // add timeout

        return calibrationFuture;
    }
}
